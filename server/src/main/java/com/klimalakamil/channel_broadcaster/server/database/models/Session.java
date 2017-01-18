package com.klimalakamil.channel_broadcaster.server.database.models;

import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 * Created by kamil on 17.01.17.
 */
public class Session extends Model {
    private Device device;
    private InetAddress address;
    private int controlPort;
    private LocalDateTime validBefore;

    public Session() {
        super();
    }

    public Session(int id, Device device, InetAddress address, int controlPort, LocalDateTime validBefore) {
        super(id);
        this.device = device;
        this.address = address;
        this.controlPort = controlPort;
        this.validBefore = validBefore;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getControlPort() {
        return controlPort;
    }

    public void setControlPort(int controlPort) {
        this.controlPort = controlPort;
    }

    public boolean isValid() {
        return validBefore.isBefore(LocalDateTime.now());
    }

    public LocalDateTime getValidBefore() {
        return validBefore;
    }

    public void setValidBefore(LocalDateTime validBefore) {
        this.validBefore = validBefore;
    }
}
