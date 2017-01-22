package com.klimalakamil.channel_broadcaster.server;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionListener;
import com.klimalakamil.channel_broadcaster.core.dispatcher.Dispatcher;
import com.klimalakamil.channel_broadcaster.core.message.AddressedParcel;
import com.klimalakamil.channel_broadcaster.core.message.processors.TextMessageBuilder;
import com.klimalakamil.channel_broadcaster.server.connection.ServerConnectionListener;

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
