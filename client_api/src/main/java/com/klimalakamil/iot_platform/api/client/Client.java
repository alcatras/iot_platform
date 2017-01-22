package com.klimalakamil.iot_platform.api.client;

import com.klimalakamil.iot_platform.core.connection.client.ClientConnection;
import com.klimalakamil.iot_platform.core.connection.client.ClientConnectionFactory;
import com.klimalakamil.iot_platform.core.dispatcher.Dispatcher;
import com.klimalakamil.iot_platform.core.message.AddressedParcel;
import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.processors.TextMessageBuilder;
import com.klimalakamil.iot_platform.core.message.serializer.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Client {

    private Logger logger = Logger.getLogger(Client.class.getName());

    private ClientConnection clientConnection;
    private Dispatcher<AddressedParcel> dispatcher;
    private JsonSerializer serializer;

    public Client(InputStream serverPKS, InputStream clientPKS, char[] password) {
        try {
            clientConnection = ClientConnectionFactory.createConnection(
                    InetAddress.getByName("localhost"),
                    25535
            );
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        dispatcher = new Dispatcher<>();
        TextMessageBuilder messageBuilder = new TextMessageBuilder(clientConnection, dispatcher);
        clientConnection.getReceiveDispatcher().registerParser(messageBuilder);

        serializer = new JsonSerializer();

        clientConnection.start();
    }

    public void send(MessageData messageData) {
        clientConnection.send(serializer.serialize(messageData));
    }

    public void close() {
        clientConnection.close();
    }

    public ClientConnection getConnection() {
        return clientConnection;
    }

    public Dispatcher<AddressedParcel> getDispatcher() {
        return dispatcher;
    }
}
