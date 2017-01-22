package com.klimalakamil.channel_broadcaster.core.connection.client;

import com.klimalakamil.channel_broadcaster.core.connection.Connection;

import java.net.InetAddress;

/**
 * Created by kamil on 17.01.17.
 */
public abstract class ClientConnection extends Connection<ClientConnectionListener> {

    public ClientConnection() {
        super();
    }

    public abstract boolean send(byte[] data);

    public abstract InetAddress getAddress();

    public abstract int getPort();
}
