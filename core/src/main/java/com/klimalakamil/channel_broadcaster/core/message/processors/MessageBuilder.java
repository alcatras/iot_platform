package com.klimalakamil.channel_broadcaster.core.message.processors;

import com.klimalakamil.channel_broadcaster.core.connection.client.BytePacket;
import com.klimalakamil.channel_broadcaster.core.dispatcher.AbstractParser;
import com.klimalakamil.channel_broadcaster.core.dispatcher.Dispatcher;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class MessageBuilder<T> implements AbstractParser<BytePacket> {

    protected Dispatcher<T> dispatcher;

    public MessageBuilder(Dispatcher<T> dispatcher) {
        this.dispatcher = dispatcher;
    }
}
