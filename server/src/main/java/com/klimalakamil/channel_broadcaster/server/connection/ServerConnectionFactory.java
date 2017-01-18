package com.klimalakamil.channel_broadcaster.server.connection;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 17.01.17.
 */
public abstract class ServerConnectionFactory {

    public static ServerConnection createConnection(InetAddress host, int port, int backlog) throws IOException {
        return new BasicServerConnection<ServerSocket>(new ServerSocket(port, backlog, host)) {
            @Override
            protected void setup() {
            }
        };
    }

    public static ServerConnection createTLSConnection(InetAddress host, int port, int backlog, InputStream serverPKS, InputStream clientPKS, char[] password)
            throws Exception {

        SecureRandom secureRandom = new SecureRandom();

        KeyStore serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load(serverPKS, password);

        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(clientPKS, "public".toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(clientKeyStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(serverKeyStore, password);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom);

        Arrays.fill(password, Character.MIN_VALUE);

        SSLServerSocketFactory sslSocketFactory = sslContext.getServerSocketFactory();

        return new BasicServerConnection<SSLServerSocket>((SSLServerSocket) sslSocketFactory.createServerSocket(port, backlog, host)) {

            @Override
            protected void setup() {
                socket.setNeedClientAuth(true);
            }
        };
    }

    private abstract static class BasicServerConnection<T extends ServerSocket> extends ServerConnection {
        Logger logger = Logger.getLogger(BasicServerConnection.class.getName());

        T socket;

        BasicServerConnection(T socket) {
            this.socket = socket;
        }

        @Override
        protected void loop() {
            eachListener(l -> {
                try {
                    l.acceptConnection(socket.accept());
                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
            });
        }

        @Override
        protected void release() {
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
