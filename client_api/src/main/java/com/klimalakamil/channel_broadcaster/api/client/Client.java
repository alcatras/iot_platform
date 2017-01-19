package com.klimalakamil.channel_broadcaster.api.client;

import com.google.gson.Gson;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionFactory;
import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;
import com.klimalakamil.channel_broadcaster.core.message.auth.LoginResponseMsgData;

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

        clientConnection.registerListener((conn, x, chunkSize, end) -> {
            Gson gson = new Gson();
            MessageDataWrapper wrapper = gson.fromJson(new String(x, 0, chunkSize), MessageDataWrapper.class);
            System.out.println(wrapper.getContent(LoginResponseMsgData.class).status);
        });

        clientConnection.start();
    }

    public void send(byte[] bytes) {
        clientConnection.send(bytes);
    }
}
