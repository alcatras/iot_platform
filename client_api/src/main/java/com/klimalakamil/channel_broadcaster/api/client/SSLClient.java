package com.klimalakamil.channel_broadcaster.api.client;


import com.klimalakamil.channel_broadcaster.core.ssl.ConnectionListener;
import com.klimalakamil.channel_broadcaster.core.ssl.SSLClientThread;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-02.
 */
class SSLClient extends SSLClientThread {

    private SSLClientSettings settings;

    private ConnectionListener connectionListener;

    private Logger logger = Logger.getLogger(SSLClient.class.getName());

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
            logger.log(Level.SEVERE, "Failed to create SSL context: " + e.getMessage(), e);
            return null;
        }
    }

    protected SSLSocket setupSSLSocket(SSLContext sslContext) {
        try {
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return (SSLSocket) sslSocketFactory.createSocket(settings.getInetAddress(), settings.getPort());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create SSL socket: " + e.getMessage(), e);
            return null;
        }
    }
}
