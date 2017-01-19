package com.klimalakamil.channel_broadcaster.server;


import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;
import com.klimalakamil.channel_broadcaster.server.connection.ServerConnection;
import com.klimalakamil.channel_broadcaster.server.connection.ServerConnectionFactory;
import com.klimalakamil.channel_broadcaster.server.core_service.AuthenticationService;
import com.klimalakamil.channel_broadcaster.server.database.DatabaseHelper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.DeviceMapper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.SessionMapper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.UserMapper;
import com.klimalakamil.channel_broadcaster.server.database.models.User;
import com.klimalakamil.channel_broadcaster.server.dispatcher.Dispatcher;

import java.net.InetAddress;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Server {

    private Logger logger = Logger.getLogger(Server.class.getName());
    private ServerConnection serverConnection;

    private Server() throws Exception {

        // Create database
        DatabaseHelper databaseHelper = new DatabaseHelper("jdbc:MySql://localhost:3306/java2016", "tester", "password");

        // Create mappers
        UserMapper userMapper = new UserMapper(databaseHelper);
        DeviceMapper deviceMapper = new DeviceMapper(databaseHelper);
        SessionMapper sessionMapper = new SessionMapper(databaseHelper);

        // Create connection
        serverConnection = ServerConnectionFactory.createConnection(
                InetAddress.getByName("localhost"),
                25535,
                10
        );

        // Create core services dispatcher
        Dispatcher<MessageDataWrapper> controlDispatcher = new Dispatcher<>();
        serverConnection.registerListener(new ClientConnectionWrapper(controlDispatcher));

        // Create core services
        AuthenticationService authenticationService = new AuthenticationService();
        controlDispatcher.registerParser(authenticationService);



        // Start server socket
        serverConnection.start();
    }

    public static void main(String[] args) throws Exception {
        //System.setProperty("javax.net.debug", "all");
        Logger logger = Logger.getLogger(Server.class.getName() + "::main");

        logger.log(Level.INFO, "Starting server");

        new Server();
    }
}
