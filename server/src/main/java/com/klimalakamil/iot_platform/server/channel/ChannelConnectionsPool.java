package com.klimalakamil.iot_platform.server.channel;

import com.klimalakamil.iot_platform.server.control.ConnectionRegistry;

import java.util.List;

/**
 * Created by kamil on 31.01.17.
 */
public class ChannelConnectionsPool {

    private static final Object lock = new Object();
    private static ChannelConnectionsPool instance;

    private ChannelConnectionsPool() {
        super();
    }

    public static ChannelConnectionsPool getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ChannelConnectionsPool();
                }
            }
        }
        return instance;
    }

    public List<Integer> reserve(int number, Channel channel) {
        return null;
    }
}
