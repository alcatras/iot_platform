package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.server.generic.BufferedDispatcher;

import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class MessageDispatcher extends BufferedDispatcher<AddressedParcel> {

    private Logger logger = Logger.getLogger(MessageDispatcher.class.getCanonicalName());
    private ExpectedMessage expectedMessage;

    public MessageDispatcher(int serviceThreads) {
        super(serviceThreads);
        expectedMessage = ExpectedMessage.getInstance();
    }

    @Override
    public void dispatchFailed(AddressedParcel data) {
        //logger.log(Level.INFO, "Failed to dispatch message :" + data.getParcel().getTag() + ", from: " + data.getWorker().getContext().getSocket());
    }

    @Override
    public void dispatch(AddressedParcel data) {
        //logger.log(Level.INFO, "Dispatching new message: " + data.getParcel().getTag());
        if (!expectedMessage.parse(data)) {
            super.dispatch(data);
            expectedMessage.shutdown();
        }
    }
}
