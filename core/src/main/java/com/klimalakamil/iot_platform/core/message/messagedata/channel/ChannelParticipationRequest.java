package com.klimalakamil.iot_platform.core.message.messagedata.channel;

/**
 * Created by kamil on 22.01.17.
 */
public class ChannelParticipationRequest extends ChannelMessage {
    private String requester;
    private boolean canWrite;
    private boolean canRead;

    public ChannelParticipationRequest(String name, String requester, boolean canRead, boolean canWrite) {
        super(name);
        this.requester = requester;
        this.canWrite = canWrite;
        this.canRead = canRead;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    @Override
    public String toString() {
        return "ChannelParticipationRequest{" +
                "requester='" + requester + '\'' +
                ", canWrite=" + canWrite +
                ", canRead=" + canRead +
                '}';
    }
}
