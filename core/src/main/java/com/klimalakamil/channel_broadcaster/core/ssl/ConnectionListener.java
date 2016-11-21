package com.klimalakamil.channel_broadcaster.core.ssl;

/**
 * Created by ekamkli on 2016-11-20.
 */
@FunctionalInterface
public interface ConnectionListener {
    void onReceive(String message);
}
