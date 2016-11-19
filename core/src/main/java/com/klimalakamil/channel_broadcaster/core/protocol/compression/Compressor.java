package com.klimalakamil.channel_broadcaster.core.protocol.compression;

/**
 * Created by ekamkli on 2016-11-19.
 */

public interface Compressor {

    void setup();

    byte[] inflate(byte[] data);

    byte[] deflate(byte[] data);
}
