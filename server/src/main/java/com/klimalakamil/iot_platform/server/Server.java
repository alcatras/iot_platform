package com.klimalakamil.iot_platform.server;

import com.klimalakamil.iot_platform.core.v2.socket.Sockets;
import com.klimalakamil.iot_platform.server.channel.ChannelConnectionHandler;
import com.klimalakamil.iot_platform.server.control.ControlConnectionHandler;
import com.klimalakamil.iot_platform.server.database.DatabaseHelper;
import com.klimalakamil.iot_platform.server.database.mappers.DeviceMapper;
import com.klimalakamil.iot_platform.server.database.mappers.SessionMapper;
import com.klimalakamil.iot_platform.server.database.mappers.UserMapper;
import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class Server {

    static {
        Security.addProvider(new Provider());

        System.setProperty("javax.net.ssl.keyStore", "server.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

        //System.setProperty("javax.net.debug","all");
    }

    private Logger logger = Logger.getLogger(Server.class.getName());
    private ServerSocket socket;

    private boolean running = true;

    private Server() throws SQLException {
        try {
            socket = Sockets.newServerSocket(InetAddress.getByName("localhost"), 25535, 10);//newSSLServerSocket(InetAddress.getByName("localhost"), 25535, 10);
            //socket.setWantClientAuth(false);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to create socket: " + e.getMessage(), e);
        }

        // Create database
        DatabaseHelper databaseHelper = new DatabaseHelper("jdbc:MySql://localhost:3306/java2016", "test", "password");

        // Create mappers (they're auto registered)
        new UserMapper(databaseHelper);
        new DeviceMapper(databaseHelper);
        new SessionMapper(databaseHelper);

        // Create connection dispatcher
        ServerConnectionDispatcher serverConnectionDispatcher = new ServerConnectionDispatcher();

        // Create control plane handlers
        ControlConnectionHandler controlConnectionHandler = new ControlConnectionHandler();
        serverConnectionDispatcher.registerParser(controlConnectionHandler);

        // Create channel plane handlers
        ChannelConnectionHandler channelConnectionHandler = new ChannelConnectionHandler();
        serverConnectionDispatcher.registerParser(channelConnectionHandler);

        logger.log(Level.INFO, "Starting server");
        while (running) {
            Socket clientSocket = null;
            try {
                clientSocket = socket.accept();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to open client connection: " + e.getMessage(), e);
                continue;
            }
            logger.log(Level.INFO, "New client connection from: " + clientSocket);
            ClientContext clientContext = new ClientContext(clientSocket);

            serverConnectionDispatcher.dispatch(clientContext);
        }
    }

    public static void main(String[] args) throws SQLException {
        new Server();
    }

    public void stop() {
        running = false;
    }
}
