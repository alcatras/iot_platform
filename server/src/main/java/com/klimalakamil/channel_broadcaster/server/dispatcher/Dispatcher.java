package com.klimalakamil.channel_broadcaster.server.dispatcher;

import com.klimalakamil.channel_broadcaster.server.dispatcher.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamil on 18.01.17.
 */
public class Dispatcher {

    private List<AbstractParser> parsers;

    public Dispatcher() {
        parsers = new ArrayList<>();
    }

    public boolean dispatch(Message message) {
        for (AbstractParser parser : parsers) {
            if (parser.parse(message))
                return true;
        }
        return false;
    }
}
