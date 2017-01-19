package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;
import com.klimalakamil.channel_broadcaster.server.dispatcher.AbstractParser;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class CoreService implements AbstractParser<MessageDataWrapper> {

    public CoreService(Class<? extends CoreService> clazz) {
        CoreServiceRegistry.getInstance().register(clazz, this);
    }
}
