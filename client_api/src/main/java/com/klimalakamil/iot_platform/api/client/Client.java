package com.klimalakamil.iot_platform.api.client;

import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.Parcel;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.PingMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelParticipationRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelResponse;
import com.klimalakamil.iot_platform.core.v2.socket.Sockets;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Client implements Consumer<Parcel> {

    private Logger logger = Logger.getLogger(Client.class.getName());

    private ClientListener listener;
    private ConnectionThread connectionThread;

    public Client(ClientListener listener, InetAddress address, int port) throws IOException {
        this.listener = listener;

        Socket socket = Sockets.newClientSocket(address, port);
        connectionThread = new ConnectionThread(socket, this);
        new Thread(connectionThread).start();
    }

    public Client(ClientListener listener, InetAddress address, int port, InputStream serverPKS, InputStream clientPKS, char[] pwd) throws Exception {
        this.listener = listener;

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();

        KeyStore serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load(serverPKS, "public".toCharArray());

        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(clientPKS, pwd);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(serverKeyStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        keyManagerFactory.init(clientKeyStore, pwd);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom);

        Arrays.fill(pwd, Character.MIN_VALUE);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(address, port);
        socket.startHandshake();

        connectionThread = new ConnectionThread(socket, this);
        new Thread(connectionThread).start();
    }

    @Override
    public void accept(Parcel parcel) {

        String tag = parcel.getTag();

        if (tag.equals(PingMessage.class.getCanonicalName())) {
            connectionThread.send(new PingMessage());

        } else if (tag.equals(GeneralStatusMessage.class.getCanonicalName())) {
            listener.onStatusMessage(parcel.getMessageData(GeneralStatusMessage.class));

        } else if (tag.equals(ChannelParticipationRequest.class.getCanonicalName())) {
            ChannelParticipationRequest request = parcel.getMessageData(ChannelParticipationRequest.class);
            boolean state = listener.acceptChannelRequest(request);

            connectionThread.send(new GeneralStatusMessage(state ? GeneralCodes.CHANNEL_ACCEPT : GeneralCodes.CHANNEL_REFUSE), parcel.getId());

        } else if (tag.equals(NewChannelResponse.class.getCanonicalName())) {
            NewChannelResponse newChannelResponse = parcel.getMessageData(NewChannelResponse.class);
            boolean state = listener.acceptNewChannel(newChannelResponse);

            connectionThread.send(new GeneralStatusMessage(state ? GeneralCodes.CHANNEL_ACCEPT : GeneralCodes.CHANNEL_REFUSE), parcel.getId());

        } else {
            listener.parseMessage(parcel);
        }
    }

    public void send(MessageData messageData) {
        connectionThread.send(messageData);
    }

    public void close() {
        connectionThread.close();
    }
}
