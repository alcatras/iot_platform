package com.klimalakamil.channel_broadcaster.api.client;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;
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

        clientConnection.registerListener((x, chunkSize) -> System.out.println(Arrays.toString(x)));

        clientConnection.start();
    }

    public void send(String data) {
        clientConnection.send(data.getBytes());
    }
}
