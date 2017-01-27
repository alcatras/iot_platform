package com.klimalakamil.iot_platform.server.generic;

/**
 * Created by kamil on 26.01.17.
 */
public interface Parser<T> {

    boolean parse(final T data);
}
