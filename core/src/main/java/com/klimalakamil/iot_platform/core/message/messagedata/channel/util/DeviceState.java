package com.klimalakamil.iot_platform.core.message.messagedata.channel.util;

/**
 * Created by kamil on 28.01.17.
 */
public enum DeviceState {
    ACCEPTED("Accepted"),
    REFUSED("Refused"),
    TIME_OUT("Time out"),
    INACTIVE_DEVICE("Device not logged in"),
    INVALID_DEVICE("Invalid device");

    private String string;

    DeviceState(String s) {
        string = s;
    }

    public String getName() {
        return string;
    }
}
