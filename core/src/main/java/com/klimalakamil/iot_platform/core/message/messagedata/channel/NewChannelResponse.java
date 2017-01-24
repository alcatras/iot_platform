package com.klimalakamil.iot_platform.core.message.messagedata.channel;

import com.klimalakamil.iot_platform.core.message.MessageData;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kamil on 22.01.17.
 */
public class NewChannelResponse implements MessageData {

    private List<SimplePair<String, String>> devicesState;

    public NewChannelResponse(List<SimplePair<String, String>> devicesState) {
        this.devicesState = devicesState;
    }

    public List<SimplePair<String, String>> getDevicesState() {
        return devicesState;
    }

    public void setDevicesState(List<SimplePair<String, String>> devicesState) {
        this.devicesState = devicesState;
    }

    @Override
    public String toString() {
        return "NewChannelResponse{" +
                "devicesState=" + Arrays.toString(devicesState.toArray()) +
                '}';
    }
}
