package com.klimalakamil.iot_platform.core.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class Dispatcher<T> {

    private Logger logger = Logger.getLogger(Dispatcher.class.getName());

    private List<AbstractParser<T>> parsers;

    public Dispatcher() {
        parsers = new ArrayList<>();
    }

    public boolean dispatch(T message) {
        for (AbstractParser<T> parser : parsers) {
            if (parser.parse(message))
                return true;
        }
        return false;
    }

    public void registerParser(AbstractParser<T> parser) {
        parsers.add(parser);
    }

    public void unregisterParser(AbstractParser<T> parser) {
        parsers.remove(parser);
    }
}
