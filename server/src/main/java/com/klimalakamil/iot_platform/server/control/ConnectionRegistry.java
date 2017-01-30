package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.server.generic.Registry;

/**
 * Created by kamil on 26.01.17.
 */
public class ConnectionRegistry extends Registry<ClientWorker> {

    private static ConnectionRegistry instance;
    private static final Object lock = new Object();

    private ConnectionRegistry() {
        super();
    }

    public static ConnectionRegistry getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConnectionRegistry();
                }
            }
        }
        return instance;
    }
}
