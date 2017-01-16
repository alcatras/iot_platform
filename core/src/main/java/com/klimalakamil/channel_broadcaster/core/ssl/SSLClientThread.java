package com.klimalakamil.channel_broadcaster.core.ssl;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-02.
 */
public abstract class SSLClientThread implements Runnable {
    private BlockingQueue<String> messages;
    private boolean running = true;

    private ConnectionListener connectionListener;

    private Logger logger = Logger.getLogger(SSLClientThread.class.getName());

    public SSLClientThread() {
        messages = new ArrayBlockingQueue<>(5);
    }

    protected abstract SSLContext setupSSLContext();

    protected abstract SSLSocket setupSSLSocket(SSLContext sslContext);

    public final void run() {
        SSLContext sslContext = setupSSLContext();

        if (sslContext != null) {

            SSLSocket sslSocket = setupSSLSocket(sslContext);
            if (sslSocket != null) {

                try {
                    sslSocket.startHandshake();

                    InputStream inputStream = sslSocket.getInputStream();
                    OutputStream outputStream = sslSocket.getOutputStream();

                    String outMessage;

                    final int CHUNK_SIZE = 2048;

                    byte[] inputBuffer = new byte[CHUNK_SIZE];
                    int bufferPosition = 0;

                    List<byte[]> chunks = new ArrayList<>();

                    while (running) {
                        int available = inputStream.available();
                        if (available > 0) {

                            while (bufferPosition + available >= CHUNK_SIZE) {
                                int complement = CHUNK_SIZE - bufferPosition;
                                available -= inputStream.read(inputBuffer, bufferPosition, complement);
                                bufferPosition = 0;

                                chunks.add(inputBuffer);
                                inputBuffer = new byte[CHUNK_SIZE];
                            }

                            bufferPosition += inputStream.read(inputBuffer, bufferPosition, available);

                            if (inputBuffer[bufferPosition - 1] == '\n') {
                                StringBuilder stringBuilder = new StringBuilder(chunks.size() * CHUNK_SIZE + bufferPosition - 1);

                                for (byte[] chunk : chunks) {
                                    stringBuilder.append(new String(chunk, "US-ASCII"));
                                }
                                stringBuilder.append(new String(inputBuffer, 0, bufferPosition, "US-ASCII"));

                                if (connectionListener != null) {
                                    connectionListener.onReceive(stringBuilder.toString());
                                }
                            }
                        }

                        outMessage = messages.poll(2, TimeUnit.MILLISECONDS);

                        if (outMessage != null) {
                            outputStream.write(outMessage.getBytes());
                        }
                    }

                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error occurred when trying to communicate over socket: " + e.getMessage(), e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        sslSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
