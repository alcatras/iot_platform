package com.klimalakamil.channel_broadcaster.core.message;

import com.google.gson.Gson;

/**
 * Created by kamil on 18.01.17.
 */
public class MessageDataWrapper {

    public String tag;
    public byte[] data;

    public MessageDataWrapper() { }

    public MessageDataWrapper(String tag, MessageData data) {
        this.tag = tag;
        Gson gson = new Gson();
        this.data = gson.toJson(data).getBytes();
    }
}
