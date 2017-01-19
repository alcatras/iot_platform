package com.klimalakamil.channel_broadcaster.server.core_service;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kamil on 18.01.17.
 */
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

    public CoreService get(Class clazz) {
        return services.get(clazz.getCanonicalName());
    }
}