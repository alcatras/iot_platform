package com.klimalakamil.iot_platform.server.generic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kamil on 26.01.17.
 */
public abstract class Registry<T> {
    private Map<String, T> collection;
    private static Object mutex = new Object();

    protected Registry() {
        collection = Collections.synchronizedMap(new HashMap<String, T>());
    }

    public void register(Class<? extends T> clazz, T data) {
        collection.put(clazz.getCanonicalName(), data);
    }

    public void register(String id, T data) {
        collection.put(id, data);
    }

    public void unregister(Class<? extends T> clazz) {
        collection.remove(clazz.getCanonicalName());
    }

    public void unregister(String id) {
        collection.remove(id);
    }

    public T get(Class<? extends T> clazz) {
        return collection.get(clazz.getCanonicalName());
    }

    public T get(String id) {
        return collection.get(id);
    }
}
