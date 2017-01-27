package com.klimalakamil.iot_platform.server.control.service;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.server.control.AddressedParcel;
import com.klimalakamil.iot_platform.server.generic.Parser;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class Service implements Parser<AddressedParcel> {

    protected Logger logger;

    private Map<String, Consumer<AddressedParcel>> actions;

    public Service(Class<? extends Service> clazz) {
        ServiceRegistry.getInstance().register(clazz, this);
        logger = Logger.getLogger(clazz.getCanonicalName());
        logger.log(Level.INFO, "Starting " + clazz.getSimpleName());
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
