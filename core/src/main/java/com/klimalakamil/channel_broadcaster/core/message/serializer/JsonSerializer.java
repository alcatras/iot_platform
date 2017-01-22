package com.klimalakamil.channel_broadcaster.core.message.serializer;

import com.google.gson.Gson;
import com.klimalakamil.channel_broadcaster.core.message.MessageData;
import com.klimalakamil.channel_broadcaster.core.message.Parcel;

/**
 * Created by kamil on 19.01.17.
 */
public class JsonSerializer implements Serializer<MessageData, Parcel> {

    private Gson gson;

    public JsonSerializer() {
        gson = new Gson();
    }

    @Override
    public byte[] serialize(MessageData messageData) {
        Parcel parcel = new Parcel(messageData.getClass().getCanonicalName(), gson.toJson(messageData).getBytes(), gson);
        return (gson.toJson(parcel) + '\n').getBytes();
    }

    @Override
    public Parcel deserialize(byte[] data) {
        return gson.fromJson(new String(data), Parcel.class);
    }
}