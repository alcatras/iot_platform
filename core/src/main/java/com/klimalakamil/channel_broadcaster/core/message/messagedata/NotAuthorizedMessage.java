package com.klimalakamil.channel_broadcaster.core.message.messagedata;

/**
 * Created by kamil on 22.01.17.
 */
public class NotAuthorizedMessage extends GeneralStatusMessage {

    public NotAuthorizedMessage() {
        super(400, "Not authorized");
    }
}