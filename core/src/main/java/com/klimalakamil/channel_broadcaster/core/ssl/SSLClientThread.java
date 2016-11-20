package com.klimalakamil.channel_broadcaster.core.ssl;


import com.klimalakamil.channel_broadcaster.core.util.Log;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by ekamkli on 2016-11-02.
 */
public class SSLClientThread implements Runnable {

    private SSLClientSettings settings;
    private BlockingQueue<String> messages;
    private boolean running = true;

    private ConnectionListener connectionListener;

    public SSLClientThread(SSLClientSettings settings) {
        super();
        this.settings = settings;

        messages = new ArrayBlockingQueue<>(5);
    }

    private SSLContext getSSLContext() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();

        KeyStore serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load(new FileInputStream(settings.getServerPublicKeyStore()), "public".toCharArray());

        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(new FileInputStream(settings.getClientPrivateKeyStore()), settings.getClientKeyStorePassword());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(serverKeyStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        keyManagerFactory.init(clientKeyStore, settings.getClientKeyStorePassword());
        settings.setClientKeyStorePassword("00000000".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom);

        return sslContext;
    }

    public final void run() {
        SSLContext sslContext = null;
        try {
            sslContext = getSSLContext();
        } catch (Exception e) {
            Log.Error.l("Failed to create SSL context: " + e.getMessage());
            return;
        }

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = null;

        try {
            sslSocket = (SSLSocket) sslSocketFactory.createSocket(settings.getInetAddress(), settings.getPort());
            sslSocket.startHandshake();

            InputStream inputStream = sslSocket.getInputStream();
            OutputStream outputStream = sslSocket.getOutputStream();

            String outMessage;

            final int CHUNK_SIZE = 2048;

            byte[] inputBuffer = new byte[CHUNK_SIZE];
            int bufferPosition = 0;

            List<byte[]> chunks = new ArrayList<>();

            try {
                while (running) {
                    int available = inputStream.available();
                    if(available > 0) {

                        while(bufferPosition + available >= CHUNK_SIZE) {
                            int complement = CHUNK_SIZE - bufferPosition;
                            available -= inputStream.read(inputBuffer, bufferPosition, complement);
                            bufferPosition = 0;

                            chunks.add(inputBuffer);
                            inputBuffer = new byte[CHUNK_SIZE];
                        }

                        bufferPosition += inputStream.read(inputBuffer, bufferPosition, available);

                        if(inputBuffer[bufferPosition-1] == '\n') {
                            StringBuilder stringBuilder = new StringBuilder(chunks.size() * CHUNK_SIZE + bufferPosition - 1);

                            for(byte[] chunk: chunks) {
                                stringBuilder.append(new String(chunk, "US-ASCII"));
                            }
                            stringBuilder.append(new String(inputBuffer, 0, bufferPosition, "US-ASCII"));

                            if(connectionListener != null) {
                                connectionListener.onReceive(stringBuilder.toString());
                            }
                        }
                    }

                    outMessage = messages.poll(2, TimeUnit.MILLISECONDS);

                    if (outMessage != null) {
                        outputStream.write(outMessage.getBytes());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.Error.l(e.getMessage());
        } finally {
            try {
                sslSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String data) {
        try {
            messages.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }
}
