package com.klimalakamil.iot_platform.core.message.messagedata.channel;

/**
 * Created by kamil on 31.01.17.
 */
public class ChannelConnectionId extends ChannelMessage{

    private byte connectionId;

    public ChannelConnectionId(String name, byte id) {
        super(name);
        this.connectionId = id;
    }

    public byte getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(byte connectionId) {
        this.connectionId = connectionId;
    }
}
