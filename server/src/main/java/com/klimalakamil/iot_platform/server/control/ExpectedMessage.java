package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.server.generic.Parser;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class ExpectedMessage implements Parser<AddressedParcel>, Runnable {

    private static final Object lock = new Object();
    private static long _id = 1;
    private static ExpectedMessage instance;
    private final Map<ExMessageId, ExMessageData> expectedList;
    private Logger logger = Logger.getLogger(ExpectedMessage.class.getCanonicalName());
    private boolean running = true;
    private ExpectedMessage() {
        expectedList = Collections.synchronizedMap(new HashMap<>());
    }

    public static long getUniqueId() {
        synchronized (lock) {
            return _id == Long.MAX_VALUE ? 1 : ++_id;
        }
    }

    public static ExpectedMessage getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ExpectedMessage();
                    new Thread(instance).start();
                }
            }
        }
        return instance;
    }

    public ExMessageId expectMessage(long id, ClientWorker worker, OnMessageReceivedListener listener, long timeout, TimeUnit timeUnit) {
        ExMessageId messageIdValue = new ExMessageId(id, worker);

        synchronized (expectedList) {
            ExMessageData data = expectedList.get(messageIdValue);

            if (data != null) {
                data.listeners.add(listener);
            } else {
                expectedList.put(messageIdValue, new ExMessageData(listener, timeUnit.toMillis(timeout)));
            }
        }

        return messageIdValue;
    }

    public ExMessageId sendAndExpect(MessageData messageData, ClientWorker worker, OnMessageReceivedListener listener, long timeout, TimeUnit timeUnit) {
        long id = getUniqueId();
        ExMessageId exMessageId = expectMessage(id, worker, listener, timeout, timeUnit);
        worker.send(messageData, id);
        return exMessageId;
    }

    public void sendMessageAndWait(MessageData messageData, ClientWorker worker, OnMessageReceivedListener listener, long timeout, TimeUnit timeUnit) {
        long id = getUniqueId();
        ExMessageId exMessageId = expectMessage(id, worker, listener, timeout, timeUnit);

        worker.send(messageData, id);

        waitUntilDone(Collections.singletonList(exMessageId));
    }

    public void waitUntilDone(List<ExMessageId> waitList) {
        boolean done = false;
        while (!done) {
            synchronized (expectedList) {
                done = true;
                for (ExMessageId exMessageId : waitList) {
                    if (expectedList.get(exMessageId) != null) {
                        done = false;
                        break;
                    }
                }
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void cancel(long id, ClientWorker clientWorker) {
        synchronized (expectedList) {
            expectedList.remove(new ExMessageId(id, clientWorker));
        }
    }

    @Override
    public boolean parse(AddressedParcel data) {
        ExMessageId exMessageId = new ExMessageId(data.getParcel().getId(), data.getWorker());

        synchronized (expectedList) {
            ExMessageData exMessageData = expectedList.get(exMessageId);

            boolean result = false;
            if (exMessageData != null) {
                if (System.currentTimeMillis() - exMessageData.startTime <= exMessageData.millisTimeout) {
                    exMessageData.listeners.forEach(listener -> listener.receive(data));
                    result = true;

                } else {
                    exMessageData.listeners.forEach(OnMessageReceivedListener::failed);
                }
            }

            if (result) {
                expectedList.remove(exMessageId);
            }
            return result;
        }
    }

    @Override
    public void run() {

        while (running) {
            synchronized (expectedList) {
                long time = System.currentTimeMillis();

                Iterator<Map.Entry<ExMessageId, ExMessageData>> iterator = expectedList.entrySet().iterator();
                while (iterator.hasNext()) {
                    ExMessageData data = iterator.next().getValue();

                    if (time - data.startTime > data.millisTimeout) {
                        data.listeners.forEach(OnMessageReceivedListener::failed);
                        iterator.remove();
                    }
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void shutdown() {
        synchronized (lock) {
            running = false;
        }
    }

    public interface OnMessageReceivedListener {
        void receive(AddressedParcel parcel);

        void failed();
    }

    public class ExMessageId {
        long id;
        ClientWorker clientWorker;

        public ExMessageId(long id, ClientWorker clientWorker) {
            this.id = id;
            this.clientWorker = clientWorker;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ExMessageId that = (ExMessageId) o;

            if (id != that.id) return false;
            return clientWorker != null ? clientWorker == that.clientWorker : that.clientWorker == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (clientWorker != null ? clientWorker.hashCode() : 0);
            return result;
        }
    }

    private class ExMessageData {
        List<OnMessageReceivedListener> listeners;
        long startTime;
        long millisTimeout;

        public ExMessageData(OnMessageReceivedListener listener, long millisTimeout) {
            startTime = System.currentTimeMillis();
            listeners = new ArrayList<>();
            listeners.add(listener);
            this.millisTimeout = millisTimeout;
        }
    }
}
