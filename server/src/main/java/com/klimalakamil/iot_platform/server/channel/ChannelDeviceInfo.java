package com.klimalakamil.iot_platform.server.channel;

import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceProperties;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceState;
import com.klimalakamil.iot_platform.server.control.ClientWorker;

/**
 * Created by kamil on 31.01.17.
 */
public class ChannelDeviceInfo extends DeviceProperties {
    private DeviceState state;

    private ClientWorker worker;

    public ChannelDeviceInfo(DeviceProperties deviceProperties, ClientWorker worker) {
        super(deviceProperties.getName(), deviceProperties.isCanRead(), deviceProperties.isCanWrite());
        this.worker = worker;
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
