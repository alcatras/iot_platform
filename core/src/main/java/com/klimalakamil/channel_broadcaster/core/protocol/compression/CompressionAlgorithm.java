package com.klimalakamil.channel_broadcaster.core.protocol.compression;

/**
 * Created by ekamkli on 2016-11-03.
 */
public enum CompressionAlgorithm {
    ZIP(1, new ZipCompressor());

    private final int id;
    private Compressor compressor;
    private boolean initialized = false;

    CompressionAlgorithm(int id, Compressor compressor) {
        this.id = id;
        this.compressor = compressor;
    }

    private void init() {
        if (!initialized) {
            compressor.setup();
            initialized = true;
        }
    }

    public void setup() {
        init();
    }

    public byte[] inflate(byte[] data) {
        init();
        return compressor.inflate(data);

    }

    public byte[] deflate(byte[] data) {
        init();
        return compressor.deflate(data);
    }

    public int getId() {
        return id;
    }
}
