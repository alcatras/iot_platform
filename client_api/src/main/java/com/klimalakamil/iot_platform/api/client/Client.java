package com.klimalakamil.iot_platform.api.client;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.Parcel;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.PingMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.auth.LoginMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelConnectionId;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelParticipationRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelResponse;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceProperties;
import com.klimalakamil.iot_platform.core.v2.socket.Sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Client implements Consumer<Parcel> {

    private Logger logger = Logger.getLogger(Client.class.getName());

    private ClientListener listener;
    private ConnectionThread connectionThread;

    private Map<String, ChannelThread> channels;

    public Client(ClientListener listener, InetAddress address, int port) throws IOException {
        this.listener = listener;

        channels = new HashMap<>();

        Socket socket = Sockets.newClientSocket(address, port);//Sockets.newClientSocket(address, port);
        //socket.setUseClientMode(true);
        connectionThread = new ConnectionThread(socket, this);
        new Thread(connectionThread).start();
    }

    @Override
    public void accept(Parcel parcel) {

        if (parcel.checkTag(PingMessage.class)) {
            connectionThread.send(new PingMessage());

        } else if (parcel.checkTag(GeneralStatusMessage.class)) {
            listener.onStatusMessage(parcel.getMessageData(GeneralStatusMessage.class));

        } else if (parcel.checkTag(ChannelParticipationRequest.class)) {
            ChannelParticipationRequest request = parcel.getMessageData(ChannelParticipationRequest.class);
            boolean state = listener.acceptChannelRequest(request);

            connectionThread.send(
                    new GeneralStatusMessage(state ? GeneralCodes.CHANNEL_ACCEPT : GeneralCodes.CHANNEL_REFUSE),
                    parcel.getId());

        } else if (parcel.checkTag(NewChannelResponse.class)) {
            NewChannelResponse newChannelResponse = parcel.getMessageData(NewChannelResponse.class);
            boolean state = listener.acceptNewChannel(newChannelResponse);

            connectionThread.send(
                    new GeneralStatusMessage(state ? GeneralCodes.CHANNEL_ACCEPT : GeneralCodes.CHANNEL_REFUSE),
                    parcel.getId());

        } else if (parcel.checkTag(ChannelConnectionId.class)) {
            ChannelConnectionId channelConnectionId = parcel.getMessageData(ChannelConnectionId.class);

            try {
                ChannelThread channelThread = new ChannelThread(
                        Sockets.newClientSocket(InetAddress.getByName("localhost"), 25535),
                        channelConnectionId.getConnectionId(), channelConnectionId.getName());
                channels.put(channelConnectionId.getName(), channelThread);
                new Thread(channelThread).start();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Invalid server address");
            }

            logger.log(Level.INFO, "Established new channel connection: " + channelConnectionId.getName());

        } else {
            listener.parseMessage(parcel);
        }
    }

    public void login(String username, String password, String deviceName) {
        send(new LoginMessage(username, password, deviceName));
    }

    public void createChannel(String name, DeviceProperties[] devices) {
        send(new NewChannelRequest(name, devices, "", ""));
    }

    public void send(MessageData messageData) {
        connectionThread.send(messageData);
    }

    public void sendOnChannel(String channelName, byte[] data) {
        ChannelThread channelThread = channels.get(channelName);
        channelThread.send(data);
    }

    public void sendTextOnChannel(String channel, String message) {
        sendOnChannel(channel, (message + '\n').getBytes());
    }

    public void close() {
        connectionThread.close();
    }
}
