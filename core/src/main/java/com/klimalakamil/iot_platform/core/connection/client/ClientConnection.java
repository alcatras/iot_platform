package com.klimalakamil.iot_platform.core.connection.client;

import com.klimalakamil.iot_platform.core.connection.Connection;
import com.klimalakamil.iot_platform.core.dispatcher.Dispatcher;

import java.net.InetAddress;

/**
 * Created by kamil on 17.01.17.
 */
public abstract class ClientConnection extends Connection<ClientConnectionListener> {

    protected Dispatcher<BytePacket> receiveDispatcher;

    public ClientConnection() {
        super();
        receiveDispatcher = new Dispatcher<>();
    }

    @Override
    protected void setup() {
        eachListener(ClientConnectionListener::onCreate);
    }

    @Override
    protected void release() {
        eachListener(ClientConnectionListener::onClose);
    }

    public abstract boolean send(byte[] data);

    public abstract InetAddress getAddress();

    public abstract int getPort();

    public Dispatcher<BytePacket> getReceiveDispatcher() {
        return receiveDispatcher;
    }
}
