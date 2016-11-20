package com.klimalakamil.channel_broadcaster.core.authentication;

/**
 * Created by ekamkli on 2016-11-20.
 */
public class DeviceIdentity {

    private int vendorId;
    private int deviceType;
    private int deviceId;

    private String user;

    public DeviceIdentity(int vendorId, int deviceType, int deviceId, String user) {
        this.vendorId = vendorId;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.user = user;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
