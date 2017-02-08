package com.klimalakamil.iot_platform.server.channel;

import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelConnectionId;
import com.klimalakamil.iot_platform.server.ClientContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by kamil on 31.01.17.
 */
public class ChannelManager {

    private static final Object lock = new Object();
    private static byte _id = 1;
    private static ChannelManager instance;
    private Map<Byte, Channel> connections;
    private List<Channel> channels;
    private Executor executor;

    private ChannelManager() {
        super();
        connections = new HashMap<>();
        channels = new ArrayList<>();
        executor = Executors.newFixedThreadPool(10);
    }

    public static byte getUniqueId() {
        synchronized (lock) {
            return _id == Byte.MAX_VALUE ? 1 : ++_id;
        }
    }

    public static ChannelManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ChannelManager();
                }
            }
        }
        return instance;
    }

    public void createChannel(String name, List<ChannelDeviceInfo> devices) {
        synchronized (lock) {
            Channel channel = new Channel(name);

            for (ChannelDeviceInfo channelDeviceInfo : devices) {
                byte id = getUniqueId();
                channel.addDevice(id, channelDeviceInfo.getWorker(), channelDeviceInfo.isCanRead(),
                        channelDeviceInfo.isCanWrite());
                channelDeviceInfo.getWorker().send(new ChannelConnectionId(name, id));
                connections.put(id, channel);
            }
            channels.add(channel);
            executor.execute(channel);
        }
    }

    public boolean addConnection(ClientContext clientContext) {
        synchronized (lock) {
            Channel channel = connections.get(clientContext.getId());
            if (channel != null) {
                channel.activateConnection(clientContext);
                return true;
            }
            return false;
        }
    }
}
