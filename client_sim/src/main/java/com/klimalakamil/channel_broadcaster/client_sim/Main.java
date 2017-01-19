package com.klimalakamil.channel_broadcaster.client_sim;

import com.klimalakamil.channel_broadcaster.api.client.Client;
import message.AddressedParcel;
import message.messagedata.GeneralStatusMessage;
import message.messagedata.auth.LoginMessage;
import org.apache.commons.cli.ParseException;

import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
                LoginMessage loginMessage = new LoginMessage("test", "password", "device");
                AddressedParcel parcel = client.expectReturn(GeneralStatusMessage.class, 3, TimeUnit.SECONDS, loginMessage);

                if (parcel != null) {
                    System.out.println(parcel.getMessageData(GeneralStatusMessage.class));
                } else {
                    System.out.println("FAILURE");
                }

                client.close();
                return;
            }
        }
    }
}
