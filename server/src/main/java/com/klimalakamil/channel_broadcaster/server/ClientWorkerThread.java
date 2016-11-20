package com.klimalakamil.channel_broadcaster.server;

import com.klimalakamil.channel_broadcaster.core.util.Log;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import java.io.*;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class ClientWorkerThread implements Runnable {

    private SSLSocket socket;
    private boolean running = true;

    public ClientWorkerThread(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            socket.startHandshake();

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            while(running) {

            }

        } catch(IOException e) {
            Log.Warning.l("Error occured during communication with client " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void stop() {
        running = false;
    }
}
