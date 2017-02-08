package com.klimalakamil.iot_platform.api.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 27.01.17.
 */
public class ChannelThread implements Runnable {

    private Logger logger = Logger.getLogger(ChannelThread.class.getCanonicalName());

    private byte id;
    private String name;
    private Socket socket;

    private BlockingQueue<byte[]> outputQueue;
    private AtomicBoolean running = new AtomicBoolean(true);

    public ChannelThread(Socket socket, byte id, String name) {
        this.id = id;
        this.name = name;
        this.socket = socket;
        outputQueue = new ArrayBlockingQueue<>(20);
    }

    public boolean send(byte[] message) {
        if (running.get()) {
            try {
                outputQueue.offer(message, 500, TimeUnit.MILLISECONDS);
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

            logger.log(Level.INFO, "Writing id: " + id);
            outputStream.write(id);
            outputStream.flush();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to initialize connection: " + e.getMessage(), e);
            return;
        }

        int available;
        int bufferPosition = 0;
        int CHUNK_SIZE = 2048;
        byte[] inputBuffer = new byte[CHUNK_SIZE];
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        while (running.get()) {
            try {
                available = inputStream.available();
                if (available > 0) {
                    while (bufferPosition + available >= CHUNK_SIZE) {
                        int remaining = CHUNK_SIZE - bufferPosition;
                        available -= inputStream.read(inputBuffer, bufferPosition, remaining);
                        bufferPosition = 0;
                        byteOutputStream.write(inputBuffer, 0, CHUNK_SIZE - 1);
                        if (inputBuffer[CHUNK_SIZE - 1] == '\n') {
                            System.out.println(
                                    "Got new message on channel " + name + ": " + byteOutputStream.toString().trim());
                            byteOutputStream.reset();
                        }
                    }

                    bufferPosition += inputStream.read(inputBuffer, bufferPosition, available);
                    byteOutputStream.write(inputBuffer, 0, available);

                    // TODO: something better
                    if (inputBuffer[bufferPosition - 1] == '\n') {
                        System.out.println(
                                "Got new message on channel " + name + ": " + byteOutputStream.toString().trim());
                        byteOutputStream.reset();
                        bufferPosition = 0;
                    }
                }

                try {
                    byte[] data;
                    while (running.get() && (data = outputQueue.poll(50, TimeUnit.MICROSECONDS)) != null) {
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

    public void close() {
        running.set(false);
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
