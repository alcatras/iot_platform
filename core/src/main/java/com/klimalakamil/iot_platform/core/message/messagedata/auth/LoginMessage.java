package com.klimalakamil.iot_platform.core.message.messagedata.auth;

import com.klimalakamil.iot_platform.core.message.MessageData;

/**
 * Created by kamil on 19.01.17.
 */
public class LoginMessage implements MessageData {

    private String username;
    private String password;
    private String device;

    public LoginMessage(String username, String password, String device) {
        this.username = username;
        this.password = password;
        this.device = device;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
