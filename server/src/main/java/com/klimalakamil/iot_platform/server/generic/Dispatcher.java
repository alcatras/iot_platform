package com.klimalakamil.iot_platform.server.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamil on 26.01.17.
 */
public abstract class Dispatcher<T> {
    private final List<Parser<T>> parsers;

    public Dispatcher() {
        parsers = new ArrayList<Parser<T>>();
    }

    public void registerParser(Parser<T> parser) {
        parsers.add(parser);
    }

    public abstract void dispatchFailed(T data);

    public void dispatch(final T data) {
        for (Parser<T> parser : parsers) {
            if (parser.parse(data))
                return;
        }
        dispatchFailed(data);
    }
}
