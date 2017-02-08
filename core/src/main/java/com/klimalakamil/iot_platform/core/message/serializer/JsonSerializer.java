package com.klimalakamil.iot_platform.core.message.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.Parcel;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 19.01.17.
 */
public class JsonSerializer {

    private Logger logger = Logger.getLogger(JsonSerializer.class.getCanonicalName());
    private Gson gson;

    public JsonSerializer() {
        gson = new Gson();
    }

    public String serialize(MessageData messageData, long id) {
        Parcel parcel = new Parcel(messageData.getClass().getCanonicalName(), id, gson.toJson(messageData).getBytes(),
                gson);
        return gson.toJson(parcel);
    }

    public String serialize(MessageData messageData) {
        return serialize(messageData, 0);
    }

    public Parcel deserialize(String data) {
        try {
            return gson.fromJson(data.replaceAll("\n", ""), Parcel.class);
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Malformed JSON: " + data);
            return null;
        }
    }
}
