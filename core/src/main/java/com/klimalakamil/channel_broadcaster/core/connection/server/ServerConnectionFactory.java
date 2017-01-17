package com.klimalakamil.channel_broadcaster.core.connection.server;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 17.01.17.
 */
public abstract class ServerConnectionFactory {

    public static ServerConnection createConnection(InetAddress host, int port, int backlog) throws IOException {
        return new BasicServerConnection<ServerSocket>(new ServerSocket(port, backlog, host)) {
            @Override
            protected void setup() {
            }

            @Override
            protected void loop() {
                try {
                    ClientConnection connection = ClientConnectionFactory.createConnection(socket.accept());
                    eachListener(l -> l.acceptConnection(connection));

                } catch (IOException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        };
    }

    private abstract static class BasicServerConnection<T extends ServerSocket> extends ServerConnection {
        Logger logger = Logger.getLogger(BasicServerConnection.class.getName());

        T socket;

        BasicServerConnection(T socket) {
            this.socket = socket;
        }

        @Override
        protected void release() {
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
