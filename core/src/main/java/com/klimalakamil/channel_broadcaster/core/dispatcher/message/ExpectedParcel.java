package com.klimalakamil.channel_broadcaster.core.dispatcher.message;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.dispatcher.AbstractParser;
import message.AddressedParcel;
import message.MessageData;

import java.util.concurrent.TimeUnit;

/**
 * Created by kamil on 19.01.17.
 */
public class ExpectedParcel implements AbstractParser<AddressedParcel> {

    private String tag;
    private AddressedParcel addressedParcel;
    private ClientConnection clientConnection;

    public ExpectedParcel(ClientConnection clientConnection) {
        tag = null;
        this.clientConnection = clientConnection;
    }

    private void setExpecting(String tag) {
        synchronized (this) {
            this.tag = tag;
        }
    }

    private void unsetExpecting() {
        synchronized (this) {
            this.tag = null;
        }
    }

    private AddressedParcel waitForParcel(long timeout, TimeUnit timeUnit) {
        long nanoTimeout = timeUnit.toNanos(timeout);
        long time = System.nanoTime();

        while (System.nanoTime() - time <= nanoTimeout) {
            synchronized (this) {
                if (addressedParcel != null) {
                    return addressedParcel;
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public AddressedParcel expect(Class<? extends MessageData> tag, long timeout, TimeUnit timeUnit) {
        setExpecting(tag.getCanonicalName());
        return waitForParcel(timeout, timeUnit);
    }

    public AddressedParcel expectReturn(Class<? extends MessageData> tag, long timeout, TimeUnit timeUnit, byte[] messageData) {
        setExpecting(tag.getCanonicalName());
        clientConnection.send(messageData);
        return waitForParcel(timeout, timeUnit);
    }

    @Override
    public boolean parse(AddressedParcel message) {
        synchronized (this) {
            if (tag == null)
                return false;

            if (message.getParcel().getTag().equals(tag)) {
                addressedParcel = message;
                unsetExpecting();
                return true;
            }
        }
        return false;
    }
}
