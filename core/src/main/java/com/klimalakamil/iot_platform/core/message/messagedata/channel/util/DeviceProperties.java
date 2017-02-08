package com.klimalakamil.iot_platform.core.message.messagedata.channel.util;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceProperties that = (DeviceProperties) o;

        if (canRead != that.canRead) return false;
        if (canWrite != that.canWrite) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (canRead ? 1 : 0);
        result = 31 * result + (canWrite ? 1 : 0);
        return result;
    }
}
