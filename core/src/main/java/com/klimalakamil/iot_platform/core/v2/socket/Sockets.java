package com.klimalakamil.iot_platform.core.v2.socket;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * Created by kamil on 26.01.17.
 */
public abstract class Sockets {

    public static Socket newClientSocket(InetAddress address, int port) throws IOException {
        return new Socket(address, port);
    }

    public static SSLSocket newSSLClientSocket(InetAddress inetAddress, int port) throws IOException {

        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(inetAddress, port);
        return sslSocket;
    }

    public static ServerSocket newServerSocket(InetAddress address, int port, int backlog) throws IOException {
        return new ServerSocket(port, backlog, address);
    }

    public static SSLServerSocket newSSLServerSocket(InetAddress address, int port, int backlog)
            throws Exception {

        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port, backlog, address);
        return sslServerSocket;

//        SecureRandom secureRandom = new SecureRandom();
//
//        KeyStore serverKeyStore = KeyStore.getInstance("JKS");
//        serverKeyStore.load(serverPKS, password);
//
//        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
//        clientKeyStore.load(clientPKS, "public".toCharArray());
//
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
//        trustManagerFactory.init(clientKeyStore);
//
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
//        keyManagerFactory.init(serverKeyStore, password);
//
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom);
//
//        Arrays.fill(password, Character.MIN_VALUE);
//
//        SSLServerSocketFactory sslSocketFactory = sslContext.getServerSocketFactory();
//
//        SSLServerSocket socket = (SSLServerSocket) sslSocketFactory.createServerSocket(port, backlog, address);
//        socket.setNeedClientAuth(true);
//
//        return socket;
    }
}
