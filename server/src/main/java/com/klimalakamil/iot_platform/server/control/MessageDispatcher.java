package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.core.message.messagedata.auth.LoginMessage;
import com.klimalakamil.iot_platform.server.generic.BufferedDispatcher;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class MessageDispatcher extends BufferedDispatcher<AddressedParcel> {

    private Logger logger = Logger.getLogger(MessageDispatcher.class.getCanonicalName());

    public MessageDispatcher(int serviceThreads) {
        super(serviceThreads);
    }

    @Override
    public void dispatchFailed(AddressedParcel data) {
        logger.log(Level.INFO, "Failed to dispatch message :" + data.getParcel().getTag() + ", from: " + data.getWorker().getContext().getSocket());
    }

    @Override
    public void dispatch(AddressedParcel data) {
        logger.log(Level.INFO, "Dispatching new message: " + data.getParcel().getTag());
        super.dispatch(data);
    }
}
