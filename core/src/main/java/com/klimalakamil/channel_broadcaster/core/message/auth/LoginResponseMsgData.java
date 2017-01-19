package com.klimalakamil.channel_broadcaster.core.message.auth;

import com.klimalakamil.channel_broadcaster.core.message.MessageData;

/**
 * Created by kamil on 19.01.17.
 */
public class LoginResponseMsgData implements MessageData {

    public String status;

    public LoginResponseMsgData(String status) {
        this.status = status;
    }
}
