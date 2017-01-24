package com.klimalakamil.iot_platform.core.dispatcher.message;

import com.klimalakamil.iot_platform.core.connection.client.ClientConnection;
import com.klimalakamil.iot_platform.core.dispatcher.AbstractParser;
import com.klimalakamil.iot_platform.core.dispatcher.Dispatcher;
import com.klimalakamil.iot_platform.core.message.AddressedParcel;
import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.serializer.JsonSerializer;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Created by kamil on 19.01.17.
 */
public class ExpectedParcel implements AbstractParser<AddressedParcel> {

    private AtomicBoolean isExpecting;

    private Map<String, Consumer<AddressedParcel>> expectedTree;
    private AddressedParcel addressedParcel;
    private ClientConnection clientConnection;

    private JsonSerializer serializer;

    public ExpectedParcel(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;

        isExpecting = new AtomicBoolean(false);
        expectedTree = new TreeMap<>();

        serializer = new JsonSerializer();
    }

    public void reset() {
        expectedTree.clear();
        addressedParcel = null;
    }

    public void addExpected(Class<? extends MessageData> clazz, Consumer<AddressedParcel> consumer) {
        expectedTree.put(clazz.getCanonicalName(), consumer);
    }

    public AddressedParcel expect(long timeout, TimeUnit timeUnit) {
        isExpecting.set(true);
        return waitForParcel(timeout, timeUnit);
    }

    public AddressedParcel expectResponse(long timeout, TimeUnit timeUnit, MessageData message) {
        isExpecting.set(true);
        clientConnection.send(serializer.serialize(message));
        return waitForParcel(timeout, timeUnit);
    }

    private AddressedParcel waitForParcel(long timeout, TimeUnit timeUnit) {
        long nanoTimeout = timeUnit.toNanos(timeout);
        long time = System.nanoTime();

        while (System.nanoTime() - time <= nanoTimeout) {
            synchronized (this) {
                if (addressedParcel != null) {
                    isExpecting.set(false);
                    return addressedParcel;
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
            }
        }
        return null;
    }

    @Override
    public boolean parse(AddressedParcel message) {
        if (!isExpecting.get())
            return false;

        synchronized (this) {
            for (String tag : expectedTree.keySet()) {
                if (tag.equals(message.getParcel().getTag())) {
                    addressedParcel = message;
                    expectedTree.get(tag).accept(message);
                    return true;
                }
            }
        }
        return false;
    }
}
