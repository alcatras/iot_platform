package com.klimalakamil.channel_broadcaster.server.message;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;

/**
 * Created by kamil on 19.01.17.
 */
public class MessageContext {

    public MessageDataWrapper wrapper;
    public ClientConnection connection;

    public MessageContext(MessageDataWrapper wrapper, ClientConnection connection) {
        this.wrapper = wrapper;
        this.connection = connection;
    }
}
