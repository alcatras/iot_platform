package com.klimalakamil.channel_broadcaster.client_sim;

import com.klimalakamil.channel_broadcaster.api.client.Client;
import com.klimalakamil.channel_broadcaster.core.dispatcher.message.ExpectedParcel;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.NotAuthorizedMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.auth.LoginMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.auth.LogoutMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.channel.DeviceProperties;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.channel.NewChannelRequest;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.time.TimeRequest;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.time.TimeResponse;
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

        ExpectedParcel expectedParcel = new ExpectedParcel(client.getConnection());
        client.getDispatcher().registerParser(expectedParcel);

        while (true) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");

            expectedParcel.reset();

            if (parts[0].equals("login")) {
                LoginMessage loginMessage = new LoginMessage("test", "password", parts[1]);

                expectedParcel.addEpectedParcel(GeneralStatusMessage.class, addressedParcel -> {
                    System.out.println(addressedParcel.getMessageData(GeneralStatusMessage.class));
                });

                expectedParcel.expectResponse(3, TimeUnit.SECONDS, loginMessage);

            } else if (parts[0].equals("logout")) {
                LogoutMessage logoutMessage = new LogoutMessage();

                expectedParcel.addEpectedParcel(GeneralStatusMessage.class, addressedParcel -> {
                    System.out.println(addressedParcel.getMessageData(GeneralStatusMessage.class));
                });

                expectedParcel.expectResponse(3, TimeUnit.SECONDS, logoutMessage);

            } else if (parts[0].equals("time")) {
                TimeRequest timeRequest = new TimeRequest();

                expectedParcel.addEpectedParcel(TimeResponse.class, addressedParcel -> {
                    System.out.println(addressedParcel.getMessageData(TimeResponse.class));
                });

                expectedParcel.addEpectedParcel(NotAuthorizedMessage.class, addressedParcel -> {
                    System.out.println("Not authorized");
                });

                expectedParcel.expectResponse(3, TimeUnit.SECONDS, timeRequest);
            } else if (parts[0].equals("channel")) {
                DeviceProperties other = new DeviceProperties(parts[1], false, false);
                NewChannelRequest channelRequest = new NewChannelRequest(new DeviceProperties[]{other}, "", "");

                expectedParcel.addEpectedParcel(GeneralStatusMessage.class, addressedParcel -> {
                    System.out.println(addressedParcel.getMessageData(GeneralStatusMessage.class));
                });

                expectedParcel.addEpectedParcel(NotAuthorizedMessage.class, addressedParcel -> {
                    System.out.println("Not authorized");
                });

                expectedParcel.expectResponse(15, TimeUnit.SECONDS, channelRequest);
            } else if (parts[0].equals("exit")) {
                break;
            }
        }
        client.close();
    }
}
