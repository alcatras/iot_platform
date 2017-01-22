package com.klimalakamil.channel_broadcaster.core.message.messagedata.channel;

/**
 * Created by kamil on 22.01.17.
 */
public class DeviceProperties {

    private String name;
    private boolean canRead;
    private boolean canWrite;

    public DeviceProperties(String name, boolean canRead, boolean canWrite) {
        this.name = name;
        this.canRead = canRead;
        this.canWrite = canWrite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }
}
