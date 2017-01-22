package com.klimalakamil.iot_platform.server;

import com.klimalakamil.iot_platform.core.connection.client.ClientConnection;
import com.klimalakamil.iot_platform.core.connection.client.ClientConnectionFactory;
import com.klimalakamil.iot_platform.core.connection.client.ClientConnectionListener;
import com.klimalakamil.iot_platform.core.dispatcher.Dispatcher;
import com.klimalakamil.iot_platform.core.message.AddressedParcel;
import com.klimalakamil.iot_platform.core.message.processors.TextMessageBuilder;
import com.klimalakamil.iot_platform.server.connection.ServerConnectionListener;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class ServerListener implements ServerConnectionListener {

    private Logger logger = Logger.getLogger(ServerListener.class.getName());
    private Dispatcher<AddressedParcel> controlDispatcher;

    private ConnectionRegistry connectionRegistry = ConnectionRegistry.getInstance();

    public ServerListener(Dispatcher<AddressedParcel> controlDispatcher) {
        this.controlDispatcher = controlDispatcher;
    }

    @Override
    public void acceptConnection(Socket socket) {
        logger.log(Level.INFO, "New client connection from: " + socket.getInetAddress().toString());
        ClientConnection connection = ClientConnectionFactory.createConnection(socket);

        connection.registerListener(new ClientConnectionListener() {
            @Override
            public void onCreate() {
                connectionRegistry.register(connection);
            }

            @Override
            public void onClose() {
                connectionRegistry.unregister(connection);
            }
        });

        TextMessageBuilder messageBuilder = new TextMessageBuilder(connection, controlDispatcher);
        connection.getReceiveDispatcher().registerParser(messageBuilder);

        connection.start();
    }
}
