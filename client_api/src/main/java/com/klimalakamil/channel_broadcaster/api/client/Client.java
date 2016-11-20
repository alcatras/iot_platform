package com.klimalakamil.channel_broadcaster.api.client;

import com.klimalakamil.channel_broadcaster.api.channel.Channel;
import com.klimalakamil.channel_broadcaster.api.channel.ChannelBuilder;
import com.klimalakamil.channel_broadcaster.api.channel.ChannelException;
import com.klimalakamil.channel_broadcaster.api.channel.ChannelPrototype;
import com.klimalakamil.channel_broadcaster.core.thread.SSLClientSettings;
import com.klimalakamil.channel_broadcaster.core.thread.SSLClientThread;

import java.io.IOException;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Client {

    public interface ClientListener {
        void onSetupFinished(boolean success);
        void onConnectionClosed();
    }

    private ClientListener clientListener;
    private SSLClientThread clientThread;

    //TODO:
    private boolean authenticated;

    public Client(SSLClientSettings settings) {

    }

    public void authenticate(DeviceIdentity identity, String login, String password) throws AuthenticationException {

    }

    public void sendControlMessage(byte[] data) throws IOException {

    }

    public ChannelBuilder getChannelBuilder() {
        return null;
    }

    public Channel openChannel(ChannelPrototype prototype) throws ChannelException {
        return null;
    }

    public void setClientListener(ClientListener clientListener) {
        this.clientListener = clientListener;
    }

    public void terminate() {

    }
}
