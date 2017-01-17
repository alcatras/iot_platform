package com.klimalakamil.channel_broadcaster.core.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Created by kamil on 17.01.17.
 */
public abstract class Connection<T extends ConnectionListener> {

    private List<T> listeners;
    private AtomicBoolean running;

    public Connection() {
        listeners = new ArrayList<>();
    }

    protected abstract void release();

    public void close() {
        running = new AtomicBoolean(false);
    }

    protected abstract void setup();

    protected abstract void loop();

    public void start() {
        setup();
        new Thread(() -> {
            while (running.get()) {
                loop();
            }
            release();
        }).start();
    }

    public void registerListener(T listener) {
        listeners.add(listener);
    }

    public void unregisterListener(T listener) {
        listeners.remove(listener);
    }

    protected void eachListener(Consumer<T> consumer) {
        listeners.forEach(consumer);
    }
}
