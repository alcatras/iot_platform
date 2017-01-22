package com.klimalakamil.iot_platform.server.connection;

import com.klimalakamil.channel_broadcaster.core.connection.ConnectionListener;

import java.net.Socket;

/**
 * Created by kamil on 17.01.17.
 */
public interface ServerConnectionListener extends ConnectionListener {

    void acceptConnection(Socket socket);
}