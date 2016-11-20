package com.klimalakamil.channel_broadcaster.api.channel;

/**
 * Created by ekamkli on 2016-11-20.
 */
public class ChannelException extends Exception {
    public ChannelException() {
    }

    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelException(Throwable cause) {
        super(cause);
    }

    public ChannelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
