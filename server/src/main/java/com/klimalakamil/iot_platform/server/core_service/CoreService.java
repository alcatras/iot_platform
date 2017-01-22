package com.klimalakamil.iot_platform.server.core_service;

import com.klimalakamil.iot_platform.core.dispatcher.AbstractParser;
import com.klimalakamil.iot_platform.core.message.AddressedParcel;
import com.klimalakamil.iot_platform.core.message.MessageData;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class CoreService implements AbstractParser<AddressedParcel> {

    private Map<String, Consumer<AddressedParcel>> actions;

    public CoreService(Class<? extends CoreService> clazz) {
        CoreServiceRegistry.getInstance().register(clazz, this);
        actions = new TreeMap<>();
    }

    protected void addAction(Class<? extends MessageData> clazz, Consumer<AddressedParcel> consumer) {
        actions.put(clazz.getCanonicalName(), consumer);
    }

    @Override
    public boolean parse(AddressedParcel addressedParcel) {
        Consumer<AddressedParcel> consumer = actions.get(addressedParcel.getParcel().getTag());
        if (consumer != null) {
            consumer.accept(addressedParcel);
            return true;
        }
        return false;
    }
}
