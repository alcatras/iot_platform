package com.klimalakamil.iot_platform.server;

import com.klimalakamil.iot_platform.server.generic.Dispatcher;

import java.io.IOException;
import java.io.InputStream;
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

        try {
            long time = System.currentTimeMillis();

            InputStream inputStream = data.getSocket().getInputStream();
            while (System.currentTimeMillis() - time < 3000) {
                if (inputStream.available() > 0) {
                    byte[] id = new byte[1];
                    inputStream.read(id, 0, 1);
                    logger.log(Level.INFO, "Connection id: " + id[0]);
                    data.setId(id[0]);

                    data.setInputStream(inputStream);
                    data.setOutputStream(data.getSocket().getOutputStream());
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
