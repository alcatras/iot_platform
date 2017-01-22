package com.klimalakamil.channel_broadcaster.core.connection.client;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 17.01.17.
 */
public abstract class ClientConnectionFactory {

    private ClientConnectionFactory() {
    }

    public static ClientConnection createConnection(Socket socket) {
        return new BasicClientConnection<>(socket);
    }

    public static ClientConnection createConnection(InetAddress host, int port) throws IOException {
        return new BasicClientConnection<>(new Socket(host, port));
    }

    public static ClientConnection createTLSConnection(SSLSocket socket) {
        return new BasicClientConnection<SSLSocket>(socket) {
            @Override
            protected void setup() {
                super.setup();
                try {
                    socket.startHandshake();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        };
    }

    public static ClientConnection createTLSConnection(InetAddress host, int port, InputStream serverPKS, InputStream clientPKS, char[] pwd)
            throws Exception {

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();

        KeyStore serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load(serverPKS, "public".toCharArray());

        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(clientPKS, pwd);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(serverKeyStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        keyManagerFactory.init(clientKeyStore, pwd);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom);

        Arrays.fill(pwd, Character.MIN_VALUE);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        return new BasicClientConnection<SSLSocket>((SSLSocket) sslSocketFactory.createSocket(host, port)) {
            @Override
            protected void setup() {
                super.setup();
                try {
                    socket.startHandshake();
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        };
    }

    private static class BasicClientConnection<T extends Socket> extends ClientConnection {
        private static final int CHUNK_SIZE = 2048;
        Logger logger = Logger.getLogger(BasicClientConnection.class.getName());
        T socket;
        InputStream inputStream;
        OutputStream outputStream;

        private byte[] inputBuffer = new byte[CHUNK_SIZE];
        private int available = 0;
        private int bufferPosition = 0;

        private BlockingQueue<byte[]> outputQueue;

        BasicClientConnection(T socket) {
            this.socket = socket;
            outputQueue = new ArrayBlockingQueue<>(10);
        }

        @Override
        protected void setup() {
            super.setup();
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Unable to get iostreams: " + e.getMessage(), e);
                close();
            }
        }

        @Override
        public boolean send(byte[] data) {
            try {
                outputQueue.offer(data, 500, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                return false;
            }
            return true;
        }

        @Override
        public InetAddress getAddress() {
            return socket.getInetAddress();
        }

        @Override
        public int getPort() {
            return socket.getPort();
        }

        @Override
        protected void release() {
            super.release();
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        @Override
        protected void loop() {
            try {
                if (socket.isClosed())
                    close();

                available = inputStream.available();
                if (available > 0) {
                    while (bufferPosition + available >= CHUNK_SIZE) {
                        int remaining = CHUNK_SIZE - bufferPosition;
                        available -= inputStream.read(inputBuffer, bufferPosition, remaining);
                        bufferPosition = 0;
                        receiveDispatcher.dispatch(new BytePacket(inputBuffer, CHUNK_SIZE - 1, inputBuffer[CHUNK_SIZE - 1] == '\n'));
                        inputBuffer = new byte[CHUNK_SIZE];
                    }

                    bufferPosition += inputStream.read(inputBuffer, bufferPosition, available);

                    // TODO: something better
                    if (inputBuffer[bufferPosition - 1] == '\n') {
                        receiveDispatcher.dispatch(new BytePacket(inputBuffer, bufferPosition - 1, true));
                        bufferPosition = 0;
                        inputBuffer = new byte[CHUNK_SIZE];
                    }
                }

                try {
                    byte[] data;
                    while ((data = outputQueue.poll(50, TimeUnit.MICROSECONDS)) != null) {
                        outputStream.write(data);
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }

            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}