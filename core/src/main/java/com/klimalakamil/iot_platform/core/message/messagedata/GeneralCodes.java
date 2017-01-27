package com.klimalakamil.iot_platform.core.message.messagedata;

/**
 * Created by kamil on 27.01.17.
 */
public enum GeneralCodes {
    OK(0, "Ok."),
    CONNECTION_TIME_OUT(10, "Connection time out."),

    UNKNOWN_ERROR(100, "Unknown error."),

    NOT_AUTHORIZED(200, "Not authorized."),

    INVALID_CREDENTIALS(300, "Invalid username or password"),
    INVALID_DEVICE(310, "Device does not exist."),
    ALREADY_LOGGED_IN(320, "Already logged in."),

    CHANNEL_ACCEPT(400, "Accept channel request."),
    CHANNEL_REFUSE(401, "Refuse channel request.");

    int code;
    String explanation;

    public String explain() {
        return "[" + code + "] " + explanation;
    }

    GeneralCodes(int code, String explanation) {
        this.code = code;
        this.explanation = explanation;
    }
}
