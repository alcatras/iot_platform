package com.klimalakamil.channel_broadcaster.api.client;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;
import com.klimalakamil.channel_broadcaster.core.dispatcher.Dispatcher;
import com.klimalakamil.channel_broadcaster.core.dispatcher.message.ExpectedParcel;
import message.AddressedParcel;
import message.MessageData;
import message.processors.MessageBuilder;
import message.processors.TextMessageBuilder;
import message.serializer.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Client {

    private Logger logger = Logger.getLogger(Client.class.getName());

    private ClientConnection clientConnection;
    private Dispatcher<AddressedParcel> dispatcher;
    private ExpectedParcel expectedParcel;
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
        MessageBuilder<AddressedParcel> messageBuilder = new TextMessageBuilder(clientConnection, dispatcher);
        clientConnection.registerListener(messageBuilder);

        expectedParcel = new ExpectedParcel(clientConnection);
        dispatcher.registerParser(expectedParcel);

        serializer = new JsonSerializer();

        clientConnection.start();
    }

    public AddressedParcel expect(Class<? extends MessageData> clazz, long timeout, TimeUnit timeUnit) {
        return expectedParcel.expect(clazz, timeout, timeUnit);
    }

    public AddressedParcel expectReturn(Class<? extends MessageData> clazz, long timeout, TimeUnit timeUnit, MessageData messageData) {
        return expectedParcel.expectReturn(clazz, timeout, timeUnit, serializer.serialize(messageData));
    }

    public void send(MessageData messageData) {
        clientConnection.send(serializer.serialize(messageData));
    }

    public void close() {
        clientConnection.close();
    }
}
