package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.Parcel;

/**
 * Created by kamil on 19.01.17.
 */
public class AddressedParcel {

    private Parcel parcel;
    private ClientWorker worker;

    public AddressedParcel(Parcel parcel, ClientWorker worker) {
        this.parcel = parcel;
        this.worker = worker;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }

    public <T> T getMessageData(Class<T> clazz) {
        return parcel.getMessageData(clazz);
    }

    public ClientWorker getWorker() {
        return worker;
    }

    public void setWorker(ClientWorker worker) {
        this.worker = worker;
    }

    public void sendBack(MessageData messageData) {
        worker.send(messageData);
    }
}
