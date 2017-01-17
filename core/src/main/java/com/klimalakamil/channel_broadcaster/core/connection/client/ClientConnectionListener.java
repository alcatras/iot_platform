package com.klimalakamil.channel_broadcaster.core.connection.client;

import com.klimalakamil.channel_broadcaster.core.connection.ConnectionListener;

/**
 * Created by kamil on 17.01.17.
 */
public interface ClientConnectionListener extends ConnectionListener {

    void receive(byte[] data);
}
