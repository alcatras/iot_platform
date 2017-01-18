package com.klimalakamil.channel_broadcaster.server.dispatcher;

import com.klimalakamil.channel_broadcaster.server.dispatcher.message.Message;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class AbstractParser {

    public AbstractParser() {

    }

    public abstract boolean parse(Message message);
}
