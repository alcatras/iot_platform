package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.server.generic.Parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class ExpectedMessage implements Parser<AddressedParcel> {

    private static long _id = 1;
    public static long getId() {
        synchronized (lock) {
            return _id == Long.MAX_VALUE ? 1 : ++_id;
        }
    }

    private static ExpectedMessage instance;
    private static final Object lock = new Object();

    private Logger logger = Logger.getLogger(ExpectedMessage.class.getCanonicalName());

    private Map<Long, Expector> expectedList;

    private ExpectedMessage() {
        expectedList = Collections.synchronizedMap(new HashMap<>());
    }

    public static ExpectedMessage getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ExpectedMessage();
                }
            }
        }
        return instance;
    }


    public void expectMessage(long id, ClientWorker worker, OnMessageReceivedListener listener) {
        if(expectedList.containsKey(id)) {
            logger.log(Level.SEVERE, "Already waiting for event with id: " + id);
        }

        expectedList.put(id, new Expector(worker, listener));
    }

    public void cancel(long id) {
        expectedList.remove(id);
    }

    @Override
    public boolean parse(AddressedParcel data) {
        Expector expector = expectedList.get(data.getParcel().getId());
        if (expector != null) {
            if(expector.clientWorker == data.getWorker())
                expector.listener.receive(data);

            expectedList.remove(data.getParcel().getId());
            return true;
        }
        return false;
    }

    public interface OnMessageReceivedListener {
        void receive(AddressedParcel parcel);
    }

    class Expector {
        ClientWorker clientWorker;
        OnMessageReceivedListener listener;

        public Expector(ClientWorker clientWorker, OnMessageReceivedListener listener) {
            this.clientWorker = clientWorker;
            this.listener = listener;
        }
    }
}
