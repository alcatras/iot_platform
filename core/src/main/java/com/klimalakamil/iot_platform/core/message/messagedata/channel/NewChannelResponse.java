package com.klimalakamil.iot_platform.core.message.messagedata.channel;

import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceState;

import java.util.Map;

/**
 * Created by kamil on 22.01.17.
 */
public class NewChannelResponse extends ChannelMessage {

    private Map<String, DeviceState> devicesState;

    public NewChannelResponse(String name, Map<String, DeviceState> devicesState) {
        super(name);
        this.devicesState = devicesState;
    }

    public Map<String, DeviceState> getDevicesState() {
        return devicesState;
    }

    public void setDevicesState(Map<String, DeviceState> devicesState) {
        this.devicesState = devicesState;
    }

    @Override
    public String toString() {
        return "NewChannelResponse{" +
                "devicesState=" + devicesState +
                '}';
    }
}
