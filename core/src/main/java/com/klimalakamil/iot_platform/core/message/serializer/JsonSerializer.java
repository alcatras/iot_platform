package com.klimalakamil.iot_platform.core.message.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.Parcel;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 19.01.17.
 */
public class JsonSerializer implements Serializer<MessageData, Parcel> {

    private Logger logger = Logger.getLogger(JsonSerializer.class.getCanonicalName());

    private Gson gson;

    public JsonSerializer() {
        gson = new Gson();
    }

    @Override
    public byte[] serialize(MessageData messageData) {
        Parcel parcel = new Parcel(messageData.getClass().getCanonicalName(), 0, gson.toJson(messageData).getBytes(), gson);
        return (gson.toJson(parcel) + '\n').getBytes();
    }

    @Override
    public Parcel deserialize(byte[] data) {
        try {
            return gson.fromJson(new String(data), Parcel.class);
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Malformed JSON: " + new String(data));
            return null;
        }
    }
}
