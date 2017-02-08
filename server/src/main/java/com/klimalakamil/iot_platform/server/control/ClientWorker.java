package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.PingMessage;
import com.klimalakamil.iot_platform.core.message.serializer.JsonSerializer;
import com.klimalakamil.iot_platform.server.ClientContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private BlockingQueue<String> messages;

    public ClientWorker(ClientContext clientContext, MessageDispatcher messageDispatcher) {
        this.context = clientContext;
        this.messageDispatcher = messageDispatcher;

        serializer = new JsonSerializer();
        messages = new ArrayBlockingQueue<>(10);
    }

    private void dispatch(String json) {
        messageDispatcher.dispatch(new AddressedParcel(serializer.deserialize(json), this));
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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getInputStream()));
        PrintWriter printWriter = new PrintWriter(context.getOutputStream());

        boolean activity;
        int checkAlive = 0;
        lastMessageTime = System.currentTimeMillis();

        while (running.get()) {
            try {
                activity = false;
                if (bufferedReader.ready()) {
                    activity = true;
                    String line = bufferedReader.readLine();
                    if (line == null)
                        break;

                    dispatch(line);
                }

                while (!messages.isEmpty()) {
                    activity = true;
                    printWriter.println(messages.poll(1, TimeUnit.MILLISECONDS));
                    printWriter.flush();
                }

                if (activity) {
                    lastMessageTime = System.currentTimeMillis();
                } else {
                    Thread.sleep(4);
                }

                if (++checkAlive > 5 * 250) {
                    printWriter.println(serializer.serialize(new PingMessage()));
                    printWriter.flush();

                    checkAlive = 0;
                    long delta = System.currentTimeMillis() - lastMessageTime;
                    if (delta > 17500) {
                        logger.log(Level.INFO,
                                "Connection timed out after " + (delta / 1000) + "s: " + context.getSocket());
                        running.set(false);

                        printWriter.println(
                                serializer.serialize(new GeneralStatusMessage(GeneralCodes.CONNECTION_TIME_OUT)));
                        printWriter.flush();
                    }
                }

            } catch (IOException e) {
                logger.log(Level.FINEST, "Error occured when communicating with client: " + e.getMessage(), e);
                break;
            } catch (InterruptedException ignored) {
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
