package com.klimalakamil.channel_broadcaster.server.message;

import com.google.gson.Gson;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;
import com.klimalakamil.channel_broadcaster.server.dispatcher.Dispatcher;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class TextMessageBuilder extends MessageBuilder<MessageContext> {

    private Logger logger = Logger.getLogger(TextMessageBuilder.class.getName());
    private StringBuilder stringBuilder;

    private Gson gson;

    public TextMessageBuilder(Dispatcher<MessageContext> dispatcher) {
        super(dispatcher);
        stringBuilder = new StringBuilder();
        gson = new Gson();
    }

    @Override
    public void receive(ClientConnection connection, byte[] data, int length, boolean end) {
        try {
            stringBuilder.append(new String(data, 0, length, "US-ASCII"));

            if (end) {
                MessageDataWrapper wrapper = gson.fromJson(stringBuilder.toString(), MessageDataWrapper.class);
                MessageContext ctxt = new MessageContext(wrapper, connection);

                dispatcher.dispatch(ctxt);
                stringBuilder = new StringBuilder();
            }
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}