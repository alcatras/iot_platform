package com.klimalakamil.channel_broadcaster.server;


import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class ClientWorkerThread implements Runnable {

    private SSLSocket socket;
    private boolean running = true;

    private Logger logger = Logger.getLogger(ClientWorkerThread.class.getName());

    public ClientWorkerThread(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            socket.startHandshake();

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            while (running) {

            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Error occured during communication with client ", e);
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
