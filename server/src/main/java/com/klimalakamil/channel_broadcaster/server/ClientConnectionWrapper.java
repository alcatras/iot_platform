package com.klimalakamil.channel_broadcaster.server;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;
import com.klimalakamil.channel_broadcaster.core.dispatcher.Dispatcher;
import com.klimalakamil.channel_broadcaster.server.connection.ServerConnectionListener;
import message.AddressedParcel;
import message.processors.MessageBuilder;
import message.processors.TextMessageBuilder;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class ClientConnectionWrapper implements ServerConnectionListener {

    private Logger logger = Logger.getLogger(ClientConnectionWrapper.class.getName());
    private Dispatcher<AddressedParcel> controlDispatcher;

    public ClientConnectionWrapper(Dispatcher<AddressedParcel> controlDispatcher) {
        this.controlDispatcher = controlDispatcher;
    }

    @Override
    public void acceptConnection(Socket socket) {
        logger.log(Level.INFO, "New client connection from: " + socket.getInetAddress().toString());
        ClientConnection connection = ClientConnectionFactory.createConnection(socket);

        MessageBuilder messageBuilder = new TextMessageBuilder(connection, controlDispatcher);
        connection.registerListener(messageBuilder);

        connection.start();
    }
}
