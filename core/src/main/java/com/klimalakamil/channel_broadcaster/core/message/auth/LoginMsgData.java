package com.klimalakamil.channel_broadcaster.core.message.auth;

import com.klimalakamil.channel_broadcaster.core.message.MessageData;

/**
 * Created by kamil on 18.01.17.
 */
public class LoginMsgData implements MessageData {

    public String login;
    public String password;
    public String deviceName;

    public LoginMsgData(String login, String password, String deviceName) {
        this.login = login;
        this.password = password;
        this.deviceName = deviceName;
    }
}
