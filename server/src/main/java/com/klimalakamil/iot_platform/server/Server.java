package com.klimalakamil.iot_platform.server;


import com.klimalakamil.iot_platform.core.dispatcher.Dispatcher;
import com.klimalakamil.iot_platform.core.message.AddressedParcel;
import com.klimalakamil.iot_platform.server.connection.ServerConnection;
import com.klimalakamil.iot_platform.server.connection.ServerConnectionFactory;
import com.klimalakamil.iot_platform.server.core_service.AuthenticationService;
import com.klimalakamil.iot_platform.server.core_service.ChannelService;
import com.klimalakamil.iot_platform.server.core_service.RoughTimeService;
import com.klimalakamil.iot_platform.server.database.DatabaseHelper;
import com.klimalakamil.iot_platform.server.database.mappers.DeviceMapper;
import com.klimalakamil.iot_platform.server.database.mappers.SessionMapper;
import com.klimalakamil.iot_platform.server.database.mappers.UserMapper;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-19.
 */
// TODO: add thread pool
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
        Dispatcher<AddressedParcel> controlDispatcher = new Dispatcher<>();
        serverConnection.registerListener(new ServerListener(controlDispatcher));

        // Create core services
        AuthenticationService authenticationService = new AuthenticationService();
        controlDispatcher.registerParser(authenticationService);

        RoughTimeService roughTimeService = new RoughTimeService();
        controlDispatcher.registerParser(roughTimeService);

        ChannelService channelService = new ChannelService();
        controlDispatcher.registerParser(channelService);

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
