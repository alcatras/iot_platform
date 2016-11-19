package com.klimalakamil.channel_broadcaster.core.protocol.compression;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class ZipCompressor implements Compressor {

    @Override
    public void setup() {

    }

    @Override
    public byte[] inflate(byte[] data) {
        return new byte[0];
    }

    @Override
    public byte[] deflate(byte[] data) {
        return new byte[0];
    }
}
