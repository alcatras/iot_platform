package com.klimalakamil.channel_broadcaster.core.connection.client;

/**
 * Created by kamil on 22.01.17.
 */
public class BytePacket {

    public byte[] data;
    public int length;
    public boolean end;

    public BytePacket(byte[] data, int length, boolean end) {
        this.data = data;
        this.length = length;
        this.end = end;
    }
}
