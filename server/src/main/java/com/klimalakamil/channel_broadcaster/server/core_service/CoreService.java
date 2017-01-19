package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.core.dispatcher.AbstractParser;
import message.AddressedParcel;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class CoreService implements AbstractParser<AddressedParcel> {

    public CoreService(Class<? extends CoreService> clazz) {
        CoreServiceRegistry.getInstance().register(clazz, this);
    }
}
