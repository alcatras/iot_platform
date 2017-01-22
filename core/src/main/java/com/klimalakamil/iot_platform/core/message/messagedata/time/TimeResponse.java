package com.klimalakamil.iot_platform.core.message.messagedata.time;

import com.klimalakamil.iot_platform.core.message.MessageData;

/**
 * Created by kamil on 22.01.17.
 */
public class TimeResponse implements MessageData {

    private String time;

    public TimeResponse(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return time;
    }
}
