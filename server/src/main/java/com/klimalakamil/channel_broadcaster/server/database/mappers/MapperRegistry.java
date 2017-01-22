package com.klimalakamil.channel_broadcaster.server.database.mappers;

import com.klimalakamil.channel_broadcaster.server.database.models.Model;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kamil on 17.01.17.
 */
// TODO: add synchronization
public final class MapperRegistry {

    private static MapperRegistry instance;
    private static Map<String, Mapper> mappers;

    private MapperRegistry() {
        mappers = new TreeMap<>();
    }

    public static MapperRegistry getInstance() {
        if (instance == null) {
            instance = new MapperRegistry();
        }
        return instance;
    }

    public void register(Class clazz, Mapper mapper) {
        mappers.put(clazz.getCanonicalName(), mapper);
    }

    public Mapper forClass(Class<? extends Model> clazz) {
        return mappers.get(clazz.getCanonicalName());
    }
}
