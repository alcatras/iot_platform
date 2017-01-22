package com.klimalakamil.iot_platform.core.message;

import com.google.gson.Gson;

/**
 * Created by kamil on 19.01.17.
 */
public class Parcel {

    private transient Gson gson;
    private String tag;
    private byte[] data;

    public Parcel() {
        gson = new Gson();
    }

    public Parcel(String tag, byte[] data, Gson gson) {
        this.tag = tag;
        this.data = data;
        this.gson = gson;
    }

    public String getTag() {
        return tag;
    }

    public <T> T getMessageData(Class<T> clazz) {
        return gson.fromJson(new String(data), clazz);
    }

    public String dump() {
        return new String(data);
    }
}
