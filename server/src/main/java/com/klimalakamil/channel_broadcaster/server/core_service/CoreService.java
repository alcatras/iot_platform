package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.server.dispatcher.AbstractParser;
import com.klimalakamil.channel_broadcaster.server.message.MessageContext;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class CoreService implements AbstractParser<MessageContext> {

    public CoreService(Class<? extends CoreService> clazz) {
        CoreServiceRegistry.getInstance().register(clazz, this);
    }
}
