package com.klimalakamil.channel_broadcaster.core.message;

import com.google.gson.Gson;

/**
 * Created by kamil on 18.01.17.
 */
public class MessageDataWrapper {

    public String tag;
    public byte[] data;
    private transient Gson gson;

    public MessageDataWrapper() {
        gson = new Gson();
    }

    public MessageDataWrapper(String tag, MessageData data) {
        this();
        this.tag = tag;
        this.data = gson.toJson(data).getBytes();
    }

    public <T> T getContent(Class<T> clazz) {
        return gson.fromJson(new String(data), clazz);
    }
}
