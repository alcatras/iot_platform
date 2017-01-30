package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.PingMessage;
import com.klimalakamil.iot_platform.core.message.serializer.JsonSerializer;
import com.klimalakamil.iot_platform.server.ClientContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class ClientWorker implements Runnable {

    private Logger logger = Logger.getLogger(ClientWorker.class.getName());
    private MessageDispatcher messageDispatcher;
    private ClientContext context;
    private JsonSerializer serializer;

    private long lastMessageTime;
    private AtomicBoolean running = new AtomicBoolean(true);

    private BlockingQueue<byte[]> messages;

    public ClientWorker(ClientContext clientContext, MessageDispatcher messageDispatcher) {
        this.context = clientContext;
        this.messageDispatcher = messageDispatcher;

        serializer = new JsonSerializer();
        messages = new ArrayBlockingQueue<>(10);
    }

    private void dispatch(ByteArrayOutputStream buffer) {
        messageDispatcher.dispatch(new AddressedParcel(serializer.deserialize(buffer.toByteArray()), this));
        buffer.reset();
    }

    public boolean send(MessageData messageData) {
        return send(messageData, 0);
    }

    public boolean send(MessageData messageData, long id) {
        if (running.get()) {
            try {
                if (!messages.offer(serializer.serialize(messageData, id), 100, TimeUnit.MILLISECONDS)) {
                    logger.log(Level.WARNING, "Unable to send message, queue full");
                    return false;
                }
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        ConnectionRegistry.getInstance().register(ClientContext.getUniqueId(context.getSocket()), this);
        logger.log(Level.INFO, "Starting control thread for connection: " + context.getSocket());


        Socket socket = context.getSocket();
        InputStream inputStream = context.getInputStream();
        OutputStream outputStream = context.getOutputStream();

        final int CHUNK_SIZE = 2048;

        int available;
        int bufferPosition = 0;
        byte buffer[] = new byte[CHUNK_SIZE];

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int checkAlive = 0;
        boolean activity;

        lastMessageTime = System.currentTimeMillis();
        while (running.get()) {
            activity = false;

            try {
                available = inputStream.available();
                if (available > 0) {
                    activity = true;
                    while (bufferPosition + available >= CHUNK_SIZE) {
                        int remaining = CHUNK_SIZE - bufferPosition;
                        available -= inputStream.read(buffer, bufferPosition, remaining);
                        bufferPosition = 0;
                        byteBuffer.write(buffer, 0, CHUNK_SIZE - 1);
                        if (buffer[CHUNK_SIZE - 1] == '\n') {
                            dispatch(byteBuffer);
                        }
                        buffer = new byte[CHUNK_SIZE];
                    }

                    bufferPosition += inputStream.read(buffer, bufferPosition, available);

                    // TODO: something better
                    if (buffer[bufferPosition - 1] == '\n') {
                        byteBuffer.write(buffer, 0, bufferPosition - 1);
                        dispatch(byteBuffer);
                        bufferPosition = 0;
                        buffer = new byte[CHUNK_SIZE];
                    }
                }

                while (!messages.isEmpty()) {
                    activity = true;
                    outputStream.write(messages.poll(1, TimeUnit.MILLISECONDS));
                }

                if (activity) {
                    lastMessageTime = System.currentTimeMillis();
                } else {
                    Thread.sleep(4);
                }

                if (++checkAlive > 5 * 250) {
                    outputStream.write(serializer.serialize(new PingMessage()));
                    checkAlive = 0;
                    long delta = System.currentTimeMillis() - lastMessageTime;
                    if (delta > 17500) {
                        logger.log(Level.INFO, "Connection timed out after " + delta + "ms: " + context.getSocket());
                        running.set(false);

                        outputStream.write(serializer.serialize(new GeneralStatusMessage(GeneralCodes.CONNECTION_TIME_OUT)));
                        outputStream.flush();
                    }
                }

            } catch (IOException | InterruptedException e) {
                logger.log(Level.FINEST, "Error occured when communicating with client: " + e.getMessage(), e);
                break;
            }
        }

        try {
            socket.close();
        } catch (IOException ignored) {
        }

        ConnectionRegistry.getInstance().unregister(ClientContext.getUniqueId(context.getSocket()));
        logger.log(Level.INFO, "Closed control thread for connection: " + context.getSocket());

    }

    public ClientContext getContext() {
        return context;
    }

    @Override
    public int hashCode() {
        return (context.getSocket().getInetAddress().getHostAddress() + ":" + context.getSocket().getPort()).hashCode();
    }

    public InetAddress getAddress() {
        return context.getSocket().getInetAddress();
    }

    public int getPort() {
        return context.getSocket().getPort();
    }
}
