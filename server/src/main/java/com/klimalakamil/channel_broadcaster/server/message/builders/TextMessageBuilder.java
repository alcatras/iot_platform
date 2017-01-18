package com.klimalakamil.channel_broadcaster.server.message.builders;

import com.klimalakamil.channel_broadcaster.server.dispatcher.Dispatcher;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class TextMessageBuilder extends MessageBuilder<String> {

    private Logger logger = Logger.getLogger(TextMessageBuilder.class.getName());
    private StringBuilder stringBuilder;

    public TextMessageBuilder(Dispatcher<String> dispatcher) {
        super(dispatcher);
        stringBuilder = new StringBuilder();
    }

    @Override
    public void receive(byte[] data, int length) {
        try {
            stringBuilder.append(new String(data, 0, length, "US-ASCII"));

            if (data[length - 1] == '\n') {
                dispatcher.dispatch(stringBuilder.toString());
            }

        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}