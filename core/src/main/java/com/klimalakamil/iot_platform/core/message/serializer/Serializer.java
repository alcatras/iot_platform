package com.klimalakamil.iot_platform.core.message.serializer;


/**
 * Created by kamil on 19.01.17.
 */
public interface Serializer<T, V> {

    byte[] serialize(T messageData);

    V deserialize(byte[] data);
}
