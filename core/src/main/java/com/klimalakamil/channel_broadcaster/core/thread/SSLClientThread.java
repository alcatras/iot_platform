package com.klimalakamil.channel_broadcaster.core.thread;


import com.klimalakamil.channel_broadcaster.core.util.Log;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;

/**
 * Created by ekamkli on 2016-11-02.
 */
public abstract class SSLClientThread implements Runnable {

    private SSLClientSettings settings;

    public SSLClientThread(SSLClientSettings settings) {
        super();
        this.settings = settings;
    }

    public final void run() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();

        KeyStore serverKeyStore = null;
        KeyStore clientKeyStore = null;

        TrustManagerFactory trustManagerFactory = null;
        KeyManagerFactory keyManagerFactory = null;
        SSLContext sslContext = null;

        try {
            serverKeyStore = KeyStore.getInstance("JKS");
            serverKeyStore.load(new FileInputStream(settings.getServerPublicKeyStore()), "public".toCharArray());

            clientKeyStore = KeyStore.getInstance("JKS");
            clientKeyStore.load(new FileInputStream(settings.getClientPrivateKeyStore()), settings.getClientKeyStorePassword());

            trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(serverKeyStore);

            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

            keyManagerFactory.init(clientKeyStore, settings.getClientKeyStorePassword());
            settings.setClientKeyStorePassword("00000000".toCharArray());

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom);

        } catch (Exception e) {
            Log.Error.l("Failed to create SSL context: " + e.getMessage());
            return;
        }

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = null;

        try {
            sslSocket = (SSLSocket) sslSocketFactory.createSocket(settings.getInetAddress(), settings.getPort());
            sslSocket.startHandshake();

            //TODO:

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
}
