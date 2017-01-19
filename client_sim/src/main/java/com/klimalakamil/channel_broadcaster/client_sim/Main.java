package com.klimalakamil.channel_broadcaster.client_sim;

import com.klimalakamil.channel_broadcaster.api.client.Client;
import com.klimalakamil.channel_broadcaster.core.message.TextMessageBuilder;
import com.klimalakamil.channel_broadcaster.core.message.auth.LoginMsgData;
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

        while (true) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("login")) {
                byte[] msg = new TextMessageBuilder()
                        .setTag(LoginMsgData.class.getCanonicalName())
                        .setMessageData(new LoginMsgData("test", "password", "device0"))
                        .getSerialized();

                client.send(msg);
            }
        }
    }
}
