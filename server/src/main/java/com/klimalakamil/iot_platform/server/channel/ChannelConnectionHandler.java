package com.klimalakamil.iot_platform.server.channel;

import com.klimalakamil.iot_platform.server.ClientContext;
import com.klimalakamil.iot_platform.server.generic.Parser;

/**
 * Created by kamil on 26.01.17.
 */
public class ChannelConnectionHandler implements Parser<ClientContext> {

    private ChannelManager channelManager;

    public ChannelConnectionHandler() {
        channelManager = ChannelManager.getInstance();
    }

    @Override
    public boolean parse(ClientContext data) {
        if (data.getId() != ClientContext.CONTROL_PLANE_ID) {
            return channelManager.addConnection(data);
        }
        return false;
    }
}
