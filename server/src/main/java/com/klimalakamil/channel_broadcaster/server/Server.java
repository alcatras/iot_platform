package com.klimalakamil.channel_broadcaster.server;


import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;
import com.klimalakamil.channel_broadcaster.server.connection.ServerConnection;
import com.klimalakamil.channel_broadcaster.server.connection.ServerConnectionFactory;
import com.klimalakamil.channel_broadcaster.server.dispatcher.builders.MessageBuilder;
import com.klimalakamil.channel_broadcaster.server.dispatcher.builders.TextMessageBuilder;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Server {

    private Logger logger = Logger.getLogger(Server.class.getName());
    private ServerConnection serverConnection;

    private Server() {

        try {
//            serverConnection = ServerConnectionFactory.createTLSConnection(
//                    InetAddress.getByName("localhost"),
//                    25535,
//                    10,
//                    getClass().getResourceAsStream("server.jks"),
//                    getClass().getResourceAsStream("cacerts.jks"),
//                    "password".toCharArray()
//            );
            serverConnection = ServerConnectionFactory.createConnection(
                    InetAddress.getByName("localhost"),
                    25535,
                    10
            );

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        serverConnection.registerListener(socket -> {
            logger.log(Level.INFO, "New client connection from: " + socket.getInetAddress().toString());
            ClientConnection connection = ClientConnectionFactory.createConnection(socket);

            MessageBuilder messageBuilder = new TextMessageBuilder();
            connection.registerListener(messageBuilder);

            connection.start();
        });

        serverConnection.start();
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        //System.setProperty("javax.net.debug", "all");
        Logger logger = Logger.getLogger(Server.class.getName() + "::main");

        logger.log(Level.INFO, "Starting server");

        new Server();
    }
}
