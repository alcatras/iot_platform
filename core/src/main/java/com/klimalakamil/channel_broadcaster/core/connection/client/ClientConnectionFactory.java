package com.klimalakamil.channel_broadcaster.core.connection.client;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 17.01.17.
 */
public abstract class ClientConnectionFactory {

    private ClientConnectionFactory() {
    }

    public static ClientConnection createConnection(Socket socket) {
        return new BasicClientConnection<Socket>(socket);
    }

    public static ClientConnection createConnection(InetAddress host, int port) throws IOException {
        return new BasicClientConnection<Socket>(new Socket(host, port));
    }

    public static ClientConnection createTLSConnection(InetAddress host, int port, File serverPKS, File clientPKS, char[] pwd)
            throws Exception {

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();

        KeyStore serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load(new FileInputStream(serverPKS), "public".toCharArray());

        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(new FileInputStream(clientPKS), pwd);

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
                try {
                    socket.startHandshake();
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        };
    }

    private static class BasicClientConnection<T extends Socket> extends ClientConnection {
        Logger logger = Logger.getLogger(BasicClientConnection.class.getName());

        T socket;

        InputStream inputStream;
        OutputStream outputStream;

        BasicClientConnection(T socket) {
            this.socket = socket;
        }

        @Override
        protected void setup() {
        }

        @Override
        public void send(byte[] data) {
            try {
                outputStream.write(data);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        @Override
        protected void release() {
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        @Override
        protected void loop() {
            try {
                int available;
                if ((available = inputStream.available()) > 0) {
                    byte data[] = new byte[available];
                    eachListener(l -> l.receive(data));
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}