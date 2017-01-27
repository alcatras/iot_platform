package com.klimalakamil.iot_platform.api.client;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.Parcel;
import com.klimalakamil.iot_platform.core.message.serializer.JsonSerializer;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import jdk.nashorn.internal.ir.Block;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private BlockingQueue<byte[]> outputQueue;
    private AtomicBoolean running = new AtomicBoolean(true);

    public ConnectionThread(Socket socket, Consumer<Parcel> consumer) {
        this.socket = socket;
        this.consumer = consumer;
        this.serializer = new JsonSerializer();

        outputQueue = new ArrayBlockingQueue<byte[]>(20);
    }

    public boolean send(MessageData messageData) {
        if(running.get()) {
            try {
                outputQueue.offer(serializer.serialize(messageData), 500, TimeUnit.MILLISECONDS);
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

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            outputStream.write(new byte[] {0, 0} );
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to initialize connection: " + e.getMessage(), e);
            return;
        }

        int available;
        int bufferPosition = 0;
        int CHUNK_SIZE = 2048;
        byte[] inputBuffer = new byte[CHUNK_SIZE];
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        while(running.get()) {
            try {
                available = inputStream.available();
                if (available > 0) {
                    while (bufferPosition + available >= CHUNK_SIZE) {
                        int remaining = CHUNK_SIZE - bufferPosition;
                        available -= inputStream.read(inputBuffer, bufferPosition, remaining);
                        bufferPosition = 0;
                        byteOutputStream.write(inputBuffer, 0, CHUNK_SIZE - 1);
                        if (inputBuffer[CHUNK_SIZE - 1] == '\n') {
                            consumer.accept(serializer.deserialize(byteOutputStream.toByteArray()));
                            byteOutputStream.reset();
                        }
                    }

                    bufferPosition += inputStream.read(inputBuffer, bufferPosition, available);
                    byteOutputStream.write(inputBuffer, 0, available);

                    // TODO: something better
                    if (inputBuffer[bufferPosition - 1] == '\n') {
                        consumer.accept(serializer.deserialize(byteOutputStream.toByteArray()));
                        byteOutputStream.reset();
                        bufferPosition = 0;
                    }
                }

                try {
                    byte[] data;
                    while ((data = outputQueue.poll(50, TimeUnit.MICROSECONDS)) != null) {
                        outputStream.write(data);
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }

            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
