package com.klimalakamil.channel_broadcaster.server.dispatcher.builders;

import com.klimalakamil.channel_broadcaster.server.dispatcher.message.TextMessage;

/**
 * Created by kamil on 18.01.17.
 */
public class TextMessageBuilder extends MessageBuilder<TextMessage> {

    @Override
    public void receive(byte[] data) {

    }

//    StringBuilder stringBuilder;
//    String readyMessage;
//
//    public TextMessageBuilder() {
//        stringBuilder = new StringBuilder();
//    }
//
//    @Override
//    public void feed(byte[] data) {
//        String part = new String(data);
//    }
//
//    @Override
//    public boolean isReady() {
//        return readyMessage != null;
//    }
//
//    @Override
//    public TextMessage getMessage() {
//        TextMessage result = new TextMessage(readyMessage);
//        readyMessage = null;
//        return result;
//    }
}
