package com.klimalakamil.channel_broadcaster.api.client;

import com.klimalakamil.channel_broadcaster.core.channel.Channel;
import com.klimalakamil.channel_broadcaster.core.channel.ChannelException;
import com.klimalakamil.channel_broadcaster.core.channel.ChannelPrototype;
import com.klimalakamil.channel_broadcaster.core.authentication.AuthenticationException;
import com.klimalakamil.channel_broadcaster.core.authentication.DeviceIdentity;
import com.klimalakamil.channel_broadcaster.core.ssl.ConnectionListener;
import com.klimalakamil.channel_broadcaster.core.ssl.SSLClientThread;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Client {

    private ConnectionListener connectionListener;
    private SSLClientThread clientThread;

    public Client(SSLClientSettings settings) {
        clientThread = new SSLClient(settings);
        new Thread(clientThread).run();
    }

    public Device authenticate(DeviceIdentity identity, String user, char[] password) throws AuthenticationException {
        
        return null;
    }

    public Channel openChannel(ChannelPrototype prototype) throws ChannelException {
        return null;
    }

    public void terminate() {

    }

    private class ClientConnectionListener implements ConnectionListener {

        @Override
        public void onReceive(String message) {

        }
    }
}
