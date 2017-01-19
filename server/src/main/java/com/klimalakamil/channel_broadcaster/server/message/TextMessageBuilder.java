package com.klimalakamil.channel_broadcaster.server.message;

import com.google.gson.Gson;
import com.klimalakamil.channel_broadcaster.core.message.MessageData;
import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;
import com.klimalakamil.channel_broadcaster.core.message.auth.LoginMessageData;
import com.klimalakamil.channel_broadcaster.server.dispatcher.Dispatcher;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class TextMessageBuilder extends MessageBuilder<MessageDataWrapper> {

    private Logger logger = Logger.getLogger(TextMessageBuilder.class.getName());
    private StringBuilder stringBuilder;

    private Gson gson;

    public TextMessageBuilder(Dispatcher<MessageDataWrapper> dispatcher) {
        super(dispatcher);
        stringBuilder = new StringBuilder();
        gson = new Gson();
    }

    @Override
    public void receive(byte[] data, int length, boolean end) {
        try {
            stringBuilder.append(new String(data, 0, length, "US-ASCII"));

            if (end) {
                MessageDataWrapper wrapper = gson.fromJson(stringBuilder.toString(), MessageDataWrapper.class);

                dispatcher.dispatch(wrapper);
                stringBuilder = new StringBuilder();
            }
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}