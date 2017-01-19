package com.klimalakamil.channel_broadcaster.client_sim;

import com.klimalakamil.channel_broadcaster.api.client.Client;
import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;
import com.klimalakamil.channel_broadcaster.core.message.TextMessage;
import com.klimalakamil.channel_broadcaster.core.message.auth.LoginMessageData;
import org.apache.commons.cli.ParseException;

import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Main {

    public static void main(String[] args) throws ParseException, UnknownHostException {

        Client client = new Client(
                Main.class.getResourceAsStream("cacerts.jks"),
                Main.class.getResourceAsStream("client.jks"),
                "password".toCharArray()
        );

        Scanner scanner = new Scanner(System.in);

        while(true) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");

            if(parts[0].equals("login")) {
                client.send(
                        new TextMessage(
                                new MessageDataWrapper(LoginMessageData.class.getCanonicalName(),
                                        new LoginMessageData("test", "password", "device0"))));
            }
        }
    }
}
