package com.klimalakamil.channel_broadcaster.api.client;

import com.klimalakamil.channel_broadcaster.core.authentication.DeviceIdentity;

/**
 * Created by ekamkli on 2016-11-20.
 */
public class Device {
    private DeviceIdentity deviceIdentity;
    private long sessionId;

    public Device(DeviceIdentity deviceIdentity, long sessionId) {
        this.deviceIdentity = deviceIdentity;
        this.sessionId = sessionId;
    }
}
