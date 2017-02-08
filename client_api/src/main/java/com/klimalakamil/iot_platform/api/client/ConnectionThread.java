package com.klimalakamil.iot_platform.api.client;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.Parcel;
import com.klimalakamil.iot_platform.core.message.serializer.JsonSerializer;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 27.01.17.
 */
public class ConnectionThread implements Runnable {

    private Logger logger = Logger.getLogger(ConnectionThread.class.getCanonicalName());

    private Socket socket;
    private Consumer<Parcel> consumer;
    private JsonSerializer serializer;

    private BlockingQueue<String> outputQueue;
    private AtomicBoolean running = new AtomicBoolean(true);

    public ConnectionThread(Socket socket, Consumer<Parcel> consumer) {
        this.socket = socket;
        this.consumer = consumer;
        this.serializer = new JsonSerializer();

        outputQueue = new ArrayBlockingQueue<>(20);
    }

    public boolean send(MessageData messageData) {
        return send(messageData, -1);
    }

    public boolean send(MessageData messageData, long id) {
        if (running.get()) {
            try {
                outputQueue.offer(serializer.serialize(messageData, id), 500, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void run() {

        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;

        try {
            OutputStream outputStream = socket.getOutputStream();

            byte[] id = new byte[]{0};
            outputStream.write(id, 0, 1);
            outputStream.flush();

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to initialize connection: " + e.getMessage(), e);
            return;
        }

        while (running.get()) {
            try {
                if (bufferedReader.ready()) {
                    consumer.accept(serializer.deserialize(bufferedReader.readLine()));
                }

                try {
                    String data;
                    while (running.get() && (data = outputQueue.poll(1, TimeUnit.MILLISECONDS)) != null) {
                        printWriter.println(data);
                        printWriter.flush();
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }

            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    public void close() {
        running.set(false);
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
