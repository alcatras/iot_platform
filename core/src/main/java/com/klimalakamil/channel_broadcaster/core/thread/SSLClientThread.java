package com.klimalakamil.channel_broadcaster.core.thread;


import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 * Created by ekamkli on 2016-11-02.
 */
public abstract class SSLClientThread implements Runnable {

    boolean running = true;
    private int port;
    private InetAddress inetAddress;

    public SSLClientThread(int port, InetAddress inetAddress) {
        super();

        this.port = port;
        this.inetAddress = inetAddress;
    }

    public final void run() {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            sslSocket = (SSLSocket) sslSocketFactory.createSocket(inetAddress, port);
            inputStream = sslSocket.getInputStream();
            outputStream = sslSocket.getOutputStream();

        } catch (IOException e) {
            setupFailed(e);
            return;
        }

        setup();
        loop(inputStream, outputStream);

        try {
            if (sslSocket != null) {
                sslSocket.close();
            }
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
