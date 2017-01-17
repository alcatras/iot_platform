package com.klimalakamil.channel_broadcaster.core.connection.server;

import com.klimalakamil.channel_broadcaster.core.connection.ConnectionListener;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;

/**
 * Created by kamil on 17.01.17.
 */
public interface ServerConnectionListener extends ConnectionListener {

    void acceptConnection(ClientConnection connection);
}