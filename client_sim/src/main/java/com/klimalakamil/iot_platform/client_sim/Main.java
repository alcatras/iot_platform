package com.klimalakamil.iot_platform.client_sim;

import com.klimalakamil.iot_platform.api.client.Client;
import com.klimalakamil.iot_platform.api.client.ClientListener;
import com.klimalakamil.iot_platform.core.message.Parcel;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.auth.LoginMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.auth.LogoutMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelParticipationRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelResponse;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceProperties;
import com.klimalakamil.iot_platform.core.message.messagedata.time.TimeRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.time.TimeResponse;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Main implements ClientListener {

    public static void main(String[] args) throws Exception {

        Main main = new Main();
        Client client = new Client(main, InetAddress.getByName("localhost"), 25535);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("login")) {
                client.send(new LoginMessage("test", "password", parts[1]));

            } else if (parts[0].equals("logout")) {
                client.send(new LogoutMessage());

            } else if (parts[0].equals("time")) {
                client.send(new TimeRequest());

            } else if (parts[0].equals("channel")) {
                DeviceProperties self = new DeviceProperties("device", true, true);
                DeviceProperties other = new DeviceProperties(parts[1], true, true);
                NewChannelRequest channelRequest = new NewChannelRequest("channel0",
                        new DeviceProperties[]{self, other}, "",
                        "");

                client.send(channelRequest);
            } else if (parts[0].equals("send")) {
                client.sendOnChannel("channel0", (parts[1] + "\n").getBytes());
            } else if (parts[0].equals("exit")) {
                client.close();
                return;
            }
        }
    }

    @Override
    public void onConnectionClose() {

    }

    @Override
    public void onStatusMessage(GeneralStatusMessage generalStatusMessage) {
        System.out.println(generalStatusMessage.getCode().explain());

        if (generalStatusMessage.getCode() == GeneralCodes.CONNECTION_TIME_OUT) {
            System.exit(0);
        }
    }

    @Override
    public boolean acceptChannelRequest(ChannelParticipationRequest request) {
        System.out.println(
                "New channel [" + request.getName() + "] request from: " + request.getRequester() + ", accepting");
        return true;
    }

    @Override
    public boolean acceptNewChannel(NewChannelResponse response) {
        System.out.println("New channel response, accepting");
        System.out.println(Arrays.toString(response.getDevicesState().entrySet().toArray()));
        return true;
    }

    @Override
    public void parseMessage(Parcel parcel) {
        System.out.println("Received: " + parcel.getTag());

        if (parcel.getTag().equals(TimeResponse.class.getCanonicalName())) {
            System.out.println(parcel.getMessageData(TimeResponse.class).getTime());
        }
    }
}
