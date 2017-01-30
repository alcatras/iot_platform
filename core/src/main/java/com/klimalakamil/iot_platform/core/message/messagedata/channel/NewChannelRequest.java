package com.klimalakamil.iot_platform.core.message.messagedata.channel;

import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceProperties;

/**
 * Created by kamil on 22.01.17.
 */
public class NewChannelRequest extends ChannelMessage {

    private DeviceProperties[] devices;
    private String security;
    private String compression;

    public NewChannelRequest(String name, DeviceProperties[] devices, String security, String compression) {
        super(name);
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
