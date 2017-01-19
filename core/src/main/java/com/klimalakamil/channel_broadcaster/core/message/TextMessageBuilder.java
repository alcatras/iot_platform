package com.klimalakamil.channel_broadcaster.core.message;

import com.google.gson.Gson;

/**
 * Created by kamil on 18.01.17.
 */
public class TextMessageBuilder {

    private MessageDataWrapper content;
    private Gson gson;

    public TextMessageBuilder() {
        content = new MessageDataWrapper();
        gson = new Gson();
    }

    public TextMessageBuilder setTag(String tag) {
        content.tag = tag;
        return this;
    }

    public TextMessageBuilder setMessageData(MessageData messageData) {
        content.data = gson.toJson(messageData).getBytes();
        return this;
    }

    public byte[] getSerialized() {
        return (gson.toJson(content) + '\n').getBytes();
    }
}
