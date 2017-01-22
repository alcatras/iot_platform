package com.klimalakamil.iot_platform.core.connection.client;

import com.klimalakamil.iot_platform.core.connection.ConnectionListener;

/**
 * Created by kamil on 17.01.17.
 */
public interface ClientConnectionListener extends ConnectionListener {

    void onCreate();

    void onClose();
}
