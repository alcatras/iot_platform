package com.klimalakamil.channel_broadcaster.core.ssl;

import com.klimalakamil.channel_broadcaster.core.util.Log;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ekamkli on 2016-10-26.
 */
public abstract class SSLServerThread implements Runnable {

    private boolean running = true;
    private SSLServerSettings settings;

    private ExecutorService threadPool;

    protected SSLServerThread(SSLServerSettings settings) {
        this.settings = settings;

        threadPool = Executors.newWorkStealingPool(settings.getMaxConnections());
    }

    public void run() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();

        KeyStore serverKeyStore = null;
        KeyStore clientKeyStore = null;

        TrustManagerFactory trustManagerFactory = null;
        KeyManagerFactory keyManagerFactory = null;

        SSLContext sslContext = null;

        try {
            serverKeyStore = KeyStore.getInstance("JKS");
            serverKeyStore.load(new FileInputStream(settings.getServerPrivateKeyStore()), settings.getServerKeyStorePassword());

            clientKeyStore = KeyStore.getInstance("JKS");
            clientKeyStore.load(new FileInputStream(settings.getClientPublicKeyStore()), "public".toCharArray());

            trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(clientKeyStore);

            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(serverKeyStore, settings.getServerKeyStorePassword());

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom);

        } catch (Exception e) {
            Log.Error.l("Failed to create SSLContext " + e.getMessage());
            return;
        }

        SSLServerSocketFactory sslSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = null;
        try {
            sslServerSocket = (SSLServerSocket) sslSocketFactory.createServerSocket(settings.getPort(), settings.getBacklogSize(), settings.getInetAddress());
            sslServerSocket.setNeedClientAuth(true);
        } catch (IOException e) {
            setupFailed(e);
            return;
        }

        setup();
        Log.Verbose.l("Server is listening on " + settings.getInetAddress().toString() + ":" + settings.getPort());

        while (running) {
            SSLSocket sslClientSocket = null;

            try {
                sslClientSocket = (SSLSocket) sslServerSocket.accept();
                Log.Verbose.l("Accepted client connection from " + sslClientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                acceptConnectionFailed(e);
                Log.Warning.l("Failed to accept client connection:" + e.getMessage());
                continue;
            }

            threadPool.execute(acceptConnection(sslClientSocket));
        }

        finish();

        try {
            Log.Verbose.l("Closing server socket");
            sslServerSocket.close();
        } catch (IOException ignored) {
        }
    }

    protected void stop() {
        running = false;
        threadPool.shutdown();
    }

    protected abstract void setup();

    protected abstract void setupFailed(Exception e);

    protected abstract Runnable acceptConnection(final SSLSocket clientSocket);

    protected abstract void acceptConnectionFailed(Exception e);

    protected abstract void finish();
}
