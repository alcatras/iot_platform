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
    private LocalDateTime validTo;

    public Session() {
        super();
    }

    public Session(Device device, InetAddress address, int controlPort, LocalDateTime validTo) {
        super();
        this.device = device;
        this.address = address;
        this.controlPort = controlPort;
        this.validTo = validTo;
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
        return validTo.isBefore(LocalDateTime.now());
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
}
