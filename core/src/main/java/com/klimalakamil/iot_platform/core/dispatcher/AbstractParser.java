package com.klimalakamil.iot_platform.core.dispatcher;

/**
 * Created by kamil on 18.01.17.
 */
public interface AbstractParser<T> {
    boolean parse(T message);
}
