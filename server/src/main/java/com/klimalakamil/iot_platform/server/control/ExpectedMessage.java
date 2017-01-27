package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.server.generic.Parser;

import java.util.*;

/**
 * Created by kamil on 26.01.17.
 */
public class ExpectedMessage implements Parser<AddressedParcel> {

    private Map<Integer, OnMessageReceivedListener> expectedList;

    public ExpectedMessage() {
        expectedList = Collections.synchronizedMap(new HashMap<>());
    }

    public void expectMessage(int id, ClientWorker clientWorker, OnMessageReceivedListener listener) {
        expectedList.put(id, listener);
    }

    @Override
    public boolean parse(AddressedParcel data) {
        int id = data.getParcel().getId();

        OnMessageReceivedListener listener = expectedList.get(id);
        if(listener != null) {
            listener.receive(data);
            return true;
        }
        return false;
    }

    public interface OnMessageReceivedListener {
        void receive(AddressedParcel parcel);
    }
}
