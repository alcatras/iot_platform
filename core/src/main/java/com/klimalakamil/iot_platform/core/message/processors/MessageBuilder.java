package com.klimalakamil.iot_platform.core.message.processors;

import com.klimalakamil.iot_platform.core.connection.client.BytePacket;
import com.klimalakamil.iot_platform.core.dispatcher.AbstractParser;
import com.klimalakamil.iot_platform.core.dispatcher.Dispatcher;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class MessageBuilder<T> implements AbstractParser<BytePacket> {

    protected Dispatcher<T> dispatcher;

    public MessageBuilder(Dispatcher<T> dispatcher) {
        this.dispatcher = dispatcher;
    }
}
