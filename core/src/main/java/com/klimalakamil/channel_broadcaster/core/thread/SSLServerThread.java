package com.klimalakamil.channel_broadcaster.core.thread;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

        while(running) {
            SSLSocket sslClientSocket = null;

            try {
                sslClientSocket = (SSLSocket) sslServerSocket.accept();
            } catch (IOException e) {
                acceptConnectionFailed(e);
                continue;
            }

            threadPool.execute(acceptConnection(sslClientSocket));
        }

        finish();

        try {
            if(sslServerSocket != null)
                sslServerSocket.close();
        } catch (IOException ignored) {
        }
    }

    protected void close() {
        running = false;
    }

    protected abstract void setup();
    protected abstract void setupFailed(Exception e);

    protected abstract Runnable acceptConnection(Socket clientSocket);
    protected abstract void acceptConnectionFailed(Exception e);

    protected abstract void finish();
}
