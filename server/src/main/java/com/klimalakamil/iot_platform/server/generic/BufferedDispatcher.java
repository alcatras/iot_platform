package com.klimalakamil.iot_platform.server.generic;

import java.util.concurrent.*;

/**
 * Created by kamil on 26.01.17.
 */
public abstract class BufferedDispatcher<T> extends Dispatcher<T> {

    private Executor executor;

    public BufferedDispatcher(int serviceThreads) {
        executor = Executors.newFixedThreadPool(serviceThreads);
    }

    @Override
    public void dispatch(final T data) {
        executor.execute(() -> super.dispatch(data));
    }
}
