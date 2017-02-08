package com.klimalakamil.iot_platform.server.channel;

import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceProperties;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceState;
import com.klimalakamil.iot_platform.server.control.ClientWorker;

/**
 * Created by kamil on 31.01.17.
 */
public class ChannelDeviceInfo {
    private String name;
    private boolean canRead;
    private boolean canWrite;
    private DeviceState state;

    private ClientWorker worker;

    public ChannelDeviceInfo(DeviceProperties deviceProperties, ClientWorker worker) {
        this.name = deviceProperties.getName();
        this.canRead = deviceProperties.isCanRead();
        this.canWrite = deviceProperties.isCanWrite();
        this.worker = worker;
    }

    public String getName() {
        return name;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public ClientWorker getWorker() {
        return worker;
    }

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState state) {
        this.state = state;
    }
}
