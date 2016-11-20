package com.klimalakamil.channel_broadcaster.core.thread;


import com.klimalakamil.channel_broadcaster.core.util.Log;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Created by ekamkli on 2016-11-02.
 */
public abstract class SSLClientThread implements Runnable {

    private boolean running = true;
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
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            sslSocket = (SSLSocket) sslSocketFactory.createSocket(settings.getInetAddress(), settings.getPort());
            sslSocket.startHandshake();
            inputStream = sslSocket.getInputStream();
            outputStream = sslSocket.getOutputStream();

        } catch (IOException e) {
            setupFailed(e);
            return;
        }

        setup();
        loop(inputStream, outputStream);

        try {
            sslSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }

    protected abstract void setup();

    protected abstract void setupFailed(Exception e);

    protected abstract void loop(InputStream inputStream, OutputStream outputStream);
}
