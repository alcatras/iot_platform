package message.messagedata;

import message.MessageData;

/**
 * Created by kamil on 19.01.17.
 */
public class GeneralStatusMessage implements MessageData {

    private int statusId;
    private String status;

    public GeneralStatusMessage() {
    }

    public GeneralStatusMessage(int statusId, String status) {
        this.statusId = statusId;
        this.status = status;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Status code: " + statusId + " (" + status + ")";
    }
}
