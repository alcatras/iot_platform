package com.klimalakamil.iot_platform.core.message.messagedata.channel;

import com.klimalakamil.iot_platform.core.message.MessageData;

/**
 * Created by kamil on 28.01.17.
 */
public class ChannelMessage implements MessageData {

    private String name;

    public ChannelMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
