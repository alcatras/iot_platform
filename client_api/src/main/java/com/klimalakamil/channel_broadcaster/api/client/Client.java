package com.klimalakamil.channel_broadcaster.api.client;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;
import message.Parcel;
import message.messagedata.GenericStatusMessage;
import message.serializer.JsonSerializer;

import java.io.ByteArrayOutputStream;
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

    public Client(InputStream serverPKS, InputStream clientPKS, char[] password) {
        try {
            clientConnection = ClientConnectionFactory.createConnection(
                    InetAddress.getByName("localhost"),
                    25535
            );
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        JsonSerializer serializer = new JsonSerializer();

        clientConnection.registerListener((x, chunkSize, end) -> {
            byteOutputStream.write(x, 0, chunkSize);

            if (end) {
                Parcel parcel = serializer.deserialize(byteOutputStream.toByteArray());
                System.out.println(parcel.getMessageData(GenericStatusMessage.class));
                byteOutputStream.reset();
            }
        });

        clientConnection.start();
    }

    public void send(byte[] bytes) {
        clientConnection.send(bytes);
    }
}
