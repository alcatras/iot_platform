package com.klimalakamil.iot_platform.client_sim;

import com.klimalakamil.iot_platform.api.client.Client;
import com.klimalakamil.iot_platform.core.dispatcher.message.ExpectedParcel;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.NotAuthorizedMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.auth.LoginMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.auth.LogoutMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelParticipationRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.DeviceProperties;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelResponse;
import com.klimalakamil.iot_platform.core.message.messagedata.time.TimeRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.time.TimeResponse;
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

        ExpectedParcel coreExpectedParcel = new ExpectedParcel(client.getConnection());
        client.getDispatcher().registerParser(coreExpectedParcel);

        coreExpectedParcel.addExpected(ChannelParticipationRequest.class, addressedParcel -> {
            client.send(new GeneralStatusMessage(0, "ok"));
        });

        coreExpectedParcel.addExpected(NewChannelResponse.class, addressedParcel -> {
            System.out.println(addressedParcel.getMessageData(NewChannelResponse.class));
        });

        coreExpectedParcel.addExpected(GeneralStatusMessage.class, addressedParcel -> {
            System.out.println(addressedParcel.getMessageData(GeneralStatusMessage.class));
        });

        coreExpectedParcel.addExpected(NotAuthorizedMessage.class, addressedParcel -> {
            System.out.println("Not authorized");
        });

        Scanner scanner = new Scanner(System.in);

        ExpectedParcel expectedParcel = new ExpectedParcel(client.getConnection());
        client.getDispatcher().registerParser(expectedParcel);

        while (true) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");

            expectedParcel.reset();

            if (parts[0].equals("login")) {
                LoginMessage loginMessage = new LoginMessage("test", "password", parts[1]);

                coreExpectedParcel.expectResponse(3, TimeUnit.SECONDS, loginMessage);

            } else if (parts[0].equals("logout")) {
                LogoutMessage logoutMessage = new LogoutMessage();

                coreExpectedParcel.expectResponse(3, TimeUnit.SECONDS, logoutMessage);

            } else if (parts[0].equals("time")) {
                TimeRequest timeRequest = new TimeRequest();

                expectedParcel.addExpected(TimeResponse.class, addressedParcel -> {
                    System.out.println(addressedParcel.getMessageData(TimeResponse.class));
                });

                expectedParcel.expectResponse(3, TimeUnit.SECONDS, timeRequest);
            } else if (parts[0].equals("channel")) {
                DeviceProperties other = new DeviceProperties(parts[1], false, false);
                NewChannelRequest channelRequest = new NewChannelRequest(new DeviceProperties[]{other}, "", "");

                coreExpectedParcel.expectResponse(15, TimeUnit.SECONDS, channelRequest);
            } else if (parts[0].equals("exit")) {
                break;
            }
        }
        client.close();
    }
}
