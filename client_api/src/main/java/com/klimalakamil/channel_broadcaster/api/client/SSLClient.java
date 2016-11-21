package com.klimalakamil.channel_broadcaster.api.client;


import com.klimalakamil.channel_broadcaster.core.ssl.ConnectionListener;
import com.klimalakamil.channel_broadcaster.core.ssl.SSLClientThread;
import com.klimalakamil.channel_broadcaster.core.util.Log;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
class SSLClient extends SSLClientThread {

    private SSLClientSettings settings;

    private ConnectionListener connectionListener;

    public SSLClient(SSLClientSettings settings) {
        super();
        this.settings = settings;
    }

    protected SSLContext setupSSLContext() {
        try {
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
        } catch (Exception e) {
            Log.Error.l("Failed to create SSL context: " + e.getMessage());
            return null;
        }
    }

    protected SSLSocket setupSSLSocket(SSLContext sslContext) {
        try {
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return (SSLSocket) sslSocketFactory.createSocket(settings.getInetAddress(), settings.getPort());
        } catch (IOException e) {
            Log.Error.l("Failed to create SSL socket: " + e.getMessage());
            return null;
        }
    }
}
