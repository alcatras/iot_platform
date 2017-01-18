package com.klimalakamil.channel_broadcaster.server.dispatcher;

/**
 * Created by kamil on 18.01.17.
 */
public interface AbstractParser<T> {
    boolean parse(T message);
}
