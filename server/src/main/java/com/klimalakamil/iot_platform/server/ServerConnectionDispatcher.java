package com.klimalakamil.iot_platform.server;

import com.klimalakamil.iot_platform.server.generic.Dispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class ServerConnectionDispatcher extends Dispatcher<ClientContext> {

    private Logger logger = Logger.getLogger(ServerConnectionDispatcher.class.getName());

    @Override
    public void dispatchFailed(ClientContext data) {

    }

    @Override
    public void dispatch(ClientContext data) {
        Socket client = data.getSocket();

        try {
            InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();

            long time = System.currentTimeMillis();

            byte[] bytes = new byte[2];
            while (System.currentTimeMillis() - time < 1000) {
                if (inputStream.available() >= 2) {
                    inputStream.read(bytes, 0, 2);

                    int id = bytes[0] & (((int) bytes[1]) << 4);

                    logger.log(Level.INFO, "Read connection id of " + id + ": " + data.getSocket().toString());

                    data.setInputStream(inputStream);
                    data.setOutputStream(outputStream);

                    data.setId(id);

                    super.dispatch(data);
                    return;
                }
                Thread.sleep(1);
            }
        } catch (IOException | InterruptedException e) {
            logger.log(Level.WARNING, "Failed to open client streams:" + e.getMessage(), e);
        }
        logger.log(Level.WARNING, "Failed to dispatch client connection, could not read connection id.");
    }
}
