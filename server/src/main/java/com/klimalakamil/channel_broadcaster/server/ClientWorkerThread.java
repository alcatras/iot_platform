package com.klimalakamil.channel_broadcaster.server;

import com.klimalakamil.channel_broadcaster.core.util.Log;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

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

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.Warning.l("Failed to open client streams: " + e.getMessage());
            running = false;
        }

        final PrintWriter printWriter = new PrintWriter(outputStream);
        socket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
            @Override
            public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
                Log.Verbose.l("Wirting to client");
                printWriter.write("elo\n");
            }
        });
        while (running) {

        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
