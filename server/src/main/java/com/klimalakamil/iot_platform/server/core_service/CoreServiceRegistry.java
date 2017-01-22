package com.klimalakamil.iot_platform.server.core_service;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kamil on 18.01.17.
 */
// TODO: add synchronization
public final class CoreServiceRegistry {

    private static CoreServiceRegistry instance;
    private static Map<String, CoreService> services;

    private CoreServiceRegistry() {
        services = new TreeMap<>();
    }

    public static CoreServiceRegistry getInstance() {
        if (instance == null) {
            instance = new CoreServiceRegistry();
        }
        return instance;
    }

    public void register(Class clazz, CoreService service) {
        services.put(clazz.getCanonicalName(), service);
    }

    public CoreService get(Class<? extends CoreService> clazz) {
        return services.get(clazz.getCanonicalName());
    }
}
