package com.klimalakamil.iot_platform.server.channel;

import com.klimalakamil.iot_platform.server.ClientContext;
import com.klimalakamil.iot_platform.server.control.ClientWorker;
import com.klimalakamil.iot_platform.server.control.ExpectedMessage;
import com.klimalakamil.iot_platform.server.generic.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class Channel implements Parser<ClientContext>, Runnable {

    private Logger logger = Logger.getLogger(Channel.class.getCanonicalName());
    private final List<ConnectionPrototype> prototypes;

    private String name;

    public Channel(String name) {
        this.name = name;
        prototypes = Collections.synchronizedList(new ArrayList<>());
    }

    public void addDevice(int connectionId, ClientWorker controlWorker, boolean read, boolean write) {
        prototypes.add(new ConnectionPrototype(connectionId, controlWorker, read, write));
    }

    public void activateConnection(ClientContext clientContext) {
        synchronized (prototypes) {
            logger.log(Level.INFO, "New client is joining channel: " + name + ": " + clientContext.getSocket());
            for(ConnectionPrototype prototype: prototypes) {
                if(prototype.getConnectionId() == clientContext.getId()) {
                    prototype.setClientContext(clientContext);
                }
            }
        }
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Starting channel thread for channel: " + name);

        boolean allActive = false;
        while(!allActive) {
            allActive = true;

            synchronized (prototypes) {
                for(ConnectionPrototype prototype: prototypes) {
                    if(prototype.getClientContext() == null) {
                        allActive = false;
                        break;
                    }
                }
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {}
        }

        logger.log(Level.INFO, "Channel " + name + ": All parties connected");

        boolean running = true;

        final int BUFFER_SIZE = 2048;
        byte buffer[] = new byte[BUFFER_SIZE];

        while(running) {
            synchronized (prototypes) {
                for(int i = 0; i < prototypes.size(); ++i) {
                    ConnectionPrototype prototype = prototypes.get(i);
                    InputStream inputStream = prototypes.get(i).getClientContext().getInputStream();

                    if(prototype.isWrite()) {
                        try {
                            int available = inputStream.available();
                            while(available > 0) {
                                int read = Math.min(available, BUFFER_SIZE);
                                available -= inputStream.read(buffer, 0, read);

                                for(int j = 0; j < prototypes.size(); ++j) {
                                    ConnectionPrototype otherPrototype = prototypes.get(j);
                                    if(i != j) {// && otherPrototype.isRead()) {
                                        OutputStream outputStream = otherPrototype.getClientContext().getOutputStream();
                                        outputStream.write(buffer, 0, read);
                                        outputStream.flush();
                                    }
                                }
                            }

                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Error when reading from channel: " + e.getMessage(), e);
                        }
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }

        logger.log(Level.INFO, "Channel " + name + " shutdown");
    }

    @Override
    public boolean parse(ClientContext data) {
        return false;
    }

    private class ConnectionPrototype {
        private int connectionId;
        private ClientWorker clientWorker;
        private ClientContext clientContext;
        private boolean read;
        private boolean write;

        public ConnectionPrototype(int connectionId, ClientWorker clientWorker, boolean read, boolean write) {
            this.connectionId = connectionId;
            this.clientWorker = clientWorker;
            this.read = read;
            this.write = write;
        }

        public int getConnectionId() {
            return connectionId;
        }

        public ClientWorker getClientWorker() {
            return clientWorker;
        }

        public boolean isRead() {
            return read;
        }

        public boolean isWrite() {
            return write;
        }

        public ClientContext getClientContext() {
            return clientContext;
        }

        public void setClientContext(ClientContext clientContext) {
            this.clientContext = clientContext;
        }
    }
}
