package com.klimalakamil.channel_broadcaster.core.thread;

import com.klimalakamil.channel_broadcaster.core.util.Log;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ekamkli on 2016-10-26.
 */
public abstract class SSLServerThread implements Runnable {

    private int port;
    private InetAddress inetAddress;

    private int backlog;
    private boolean running = true;

    private ExecutorService threadPool;

    protected SSLServerThread(int port, InetAddress inetAddress, int backlog, int maxThreads) {
        this.port = port;
        this.inetAddress = inetAddress;
        this.backlog = backlog;

        threadPool = Executors.newWorkStealingPool(maxThreads);
    }

    public void run() {
        SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket sslServerSocket = null;
        try {
            sslServerSocket = (SSLServerSocket) sslSocketFactory.createServerSocket(port, backlog, inetAddress);
        } catch (IOException e) {
            setupFailed(e);
            return;
        }

        setup();
        Log.Verbose.l("Server is listening on " + inetAddress.toString() + ":" + port);

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
            if (sslServerSocket != null)
                sslServerSocket.close();
        } catch (IOException ignored) {
        }
    }

    protected void close() {
        running = false;
    }

    protected abstract void setup();

    protected abstract void setupFailed(Exception e);

    protected abstract Runnable acceptConnection(final SSLSocket clientSocket);

    protected abstract void acceptConnectionFailed(Exception e);

    protected abstract void finish();
}
