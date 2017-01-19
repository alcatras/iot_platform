package com.klimalakamil.channel_broadcaster.core.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by kamil on 18.01.17.
 */
public class TextMessage implements Message {

    private MessageDataWrapper content;

    private Gson gson;

    public TextMessage(MessageDataWrapper content) {
        this.content = content;

        gson = new Gson();
    }

    @Override
    public byte[] serialize() {
        return (gson.toJson(content) + '\n').getBytes();
    }
}
