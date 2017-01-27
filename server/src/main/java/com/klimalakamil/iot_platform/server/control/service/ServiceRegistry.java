package com.klimalakamil.iot_platform.server.control.service;

import com.klimalakamil.iot_platform.server.generic.Registry;

/**
 * Created by kamil on 27.01.17.
 */
public class ServiceRegistry extends Registry<Service> {

    private static ServiceRegistry instance;
    private static Object lock = new Object();

    private ServiceRegistry() {
        super();
    }

    public static ServiceRegistry getInstance() {
        if(instance == null) {
            synchronized (lock) {
                if(instance == null) {
                    instance = new ServiceRegistry();
                }
            }
        }
        return instance;
    }
}
