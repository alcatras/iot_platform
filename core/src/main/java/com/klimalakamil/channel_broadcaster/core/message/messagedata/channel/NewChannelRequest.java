package com.klimalakamil.channel_broadcaster.core.message.messagedata.channel;

import com.klimalakamil.channel_broadcaster.core.message.MessageData;

/**
 * Created by kamil on 22.01.17.
 */
public class NewChannelRequest implements MessageData {

    private DeviceProperties[] devices;
    private String security;
    private String compression;

    public NewChannelRequest(DeviceProperties[] devices, String security, String compression) {
        this.devices = devices;
        this.security = security;
        this.compression = compression;
    }

    public DeviceProperties[] getDevices() {
        return devices;
    }

    public void setDevices(DeviceProperties[] devices) {
        this.devices = devices;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }
}
