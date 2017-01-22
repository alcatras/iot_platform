package com.klimalakamil.channel_broadcaster.server;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;

import java.net.InetAddress;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kamil on 22.01.17.
 */
public class ConnectionRegistry {
    private static ConnectionRegistry instance;
    private static Map<String, ClientConnection> connections;

    private ConnectionRegistry() {
        connections = new TreeMap<>();
    }

    public static ConnectionRegistry getInstance() {
        if (instance == null) {
            instance = new ConnectionRegistry();
        }
        return instance;
    }

    private String getClientConnectionId(InetAddress address, int port) {
        return address.getHostAddress() + ":" + port;
    }

    public void register(ClientConnection connection) {
        synchronized (this) {
            connections.put(getClientConnectionId(connection.getAddress(), connection.getPort()), connection);
        }
    }

    public void unregister(ClientConnection connection) {
        synchronized (this) {
            connections.remove(getClientConnectionId(connection.getAddress(), connection.getPort()));
        }
    }

    public ClientConnection get(InetAddress inetAddress, int port) {
        synchronized (this) {
            return connections.get(getClientConnectionId(inetAddress, port));
        }
    }
}
