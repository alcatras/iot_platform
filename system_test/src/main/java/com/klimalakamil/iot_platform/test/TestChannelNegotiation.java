package com.klimalakamil.iot_platform.test;

import com.klimalakamil.iot_platform.api.client.Client;
import com.klimalakamil.iot_platform.api.client.ClientListener;
import com.klimalakamil.iot_platform.core.message.Parcel;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelParticipationRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelResponse;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceProperties;
import com.klimalakamil.iot_platform.core.message.messagedata.time.TimeRequest;
import com.klimalakamil.iot_platform.server.Server;

import java.net.InetAddress;
import java.sql.SQLException;

/**
 * Created by kamil on 08.02.17.
 */
public class TestChannelNegotiation {

    public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getByName("localhost");
        int port = 25566;

        new Thread(() -> {
            try {
                Server server = new Server(address, port, 10);

                for (; ; ) {

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(1000);

        Client device0 = new Client(new TestClientListener("device"), address, port);
        Client device1 = new Client(new TestClientListener("receiver"), address, port);

        Thread.sleep(1000);

        String deviceName0 = "device";
        String deviceName1 = "receiver";

        device0.login("test", "password", deviceName0);
        device1.login("test", "password", deviceName1);

        Thread.sleep(1000);

        device0.send(new TimeRequest());

        String channelName0 = "channel0";

        DeviceProperties devices[] = new DeviceProperties[]{
                new DeviceProperties(deviceName0, true, true),
                new DeviceProperties(deviceName1, true, true)
        };

        device0.createChannel(channelName0, devices);

        Thread.sleep(2500);

        device0.sendTextOnChannel(channelName0, "message 0");

        device1.sendTextOnChannel(channelName0, "message 1");

        for (; ; ) {

        }
    }

    private static class TestClientListener implements ClientListener {

        String name;

        public TestClientListener(String name) {
            this.name = name;
        }

        @Override
        public void onConnectionClose() {
            System.out.println("Client: " + name + " closed connection");
        }

        @Override
        public void onStatusMessage(GeneralStatusMessage generalStatusMessage) {
            System.out.println(
                    "Client: " + name + " received status message: " + generalStatusMessage.getCode().explain());

        }

        @Override
        public boolean acceptChannelRequest(ChannelParticipationRequest request) {
            System.out.println(
                    "Client: " + name + " received channel participation request from: " + request.getRequester());
            return true;
        }

        @Override
        public boolean acceptNewChannel(NewChannelResponse response) {
            System.out.println("Client: " + name + " received channel accept message: " + response.getDevicesState());
            return true;
        }

        @Override
        public void parseMessage(Parcel parcel) {
            System.out.println("Client: " + name + " received: " + parcel.getTag() + " message");
        }
    }
}
