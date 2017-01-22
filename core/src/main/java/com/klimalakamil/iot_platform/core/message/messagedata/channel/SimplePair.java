package com.klimalakamil.iot_platform.core.message.messagedata.channel;

/**
 * Created by kamil on 22.01.17.
 */
public class SimplePair<T, V> {

    private T first;
    private V second;

    public SimplePair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }
}
