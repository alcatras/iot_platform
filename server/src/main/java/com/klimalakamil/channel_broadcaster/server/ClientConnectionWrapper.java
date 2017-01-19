package com.klimalakamil.channel_broadcaster.server;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;
import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;
import com.klimalakamil.channel_broadcaster.server.connection.ServerConnectionListener;
import com.klimalakamil.channel_broadcaster.server.dispatcher.Dispatcher;
import com.klimalakamil.channel_broadcaster.server.message.MessageBuilder;
import com.klimalakamil.channel_broadcaster.server.message.TextMessageBuilder;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class ClientConnectionWrapper implements ServerConnectionListener {

    private Logger logger = Logger.getLogger(ClientConnectionWrapper.class.getName());
    private Dispatcher<MessageDataWrapper> controlDispatcher;

    public ClientConnectionWrapper(Dispatcher<MessageDataWrapper> controlDispatcher) {
        this.controlDispatcher = controlDispatcher;
    }

    @Override
    public void acceptConnection(Socket socket) {
        logger.log(Level.INFO, "New client connection from: " + socket.getInetAddress().toString());
        ClientConnection connection = ClientConnectionFactory.createConnection(socket);

        MessageBuilder messageBuilder = new TextMessageBuilder(controlDispatcher);
        connection.registerListener(messageBuilder);

        connection.start();
    }
}
