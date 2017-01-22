package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.message.MessageData;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.NotAuthorizedMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.channel.DeviceProperties;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.channel.NewChannelRequest;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.channel.SimplePair;
import com.klimalakamil.channel_broadcaster.core.message.serializer.JsonSerializer;
import com.klimalakamil.channel_broadcaster.server.ConnectionRegistry;
import com.klimalakamil.channel_broadcaster.server.database.mappers.DeviceMapper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.MapperRegistry;
import com.klimalakamil.channel_broadcaster.server.database.mappers.SessionMapper;
import com.klimalakamil.channel_broadcaster.server.database.models.Device;
import com.klimalakamil.channel_broadcaster.server.database.models.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamil on 22.01.17.
 */
public class ChannelService extends CoreService {

    private AuthenticationService authenticationService = (AuthenticationService) CoreServiceRegistry.getInstance().get(AuthenticationService.class);
    private DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Device.class);
    private SessionMapper sessionMapper = (SessionMapper) MapperRegistry.getInstance().forClass(Session.class);
    private ConnectionRegistry connectionRegistry = ConnectionRegistry.getInstance();
    private JsonSerializer serializer = new JsonSerializer();

    public ChannelService() {
        super(ChannelService.class);

        addAction(NewChannelRequest.class, addressedParcel -> {
            ClientConnection connection = addressedParcel.getConnection();
            Session clientSession = sessionMapper.get(connection);

            if (authenticationService.isActive(clientSession)) {
                NewChannelRequest channelRequest = addressedParcel.getMessageData(NewChannelRequest.class);
                Device rootDevice = clientSession.getDevice();

                List<SimplePair<String, String>> devicesState = new ArrayList<>();

                for (DeviceProperties deviceProperties : channelRequest.getDevices()) {
                    Device device = deviceMapper.get(rootDevice.getUser(), deviceProperties.getName());
                    Session session = sessionMapper.get(device);

                    if (device != null) {
                        if (session.isValid()) {
                            ClientConnection deviceConnection = connectionRegistry.get(session.getAddress(), session.getControlPort());
                            send(deviceConnection, new GeneralStatusMessage(55, "o dziala"));
                        } else {
                            devicesState.add(new SimplePair<>(deviceProperties.getName(), "not logged in"));
                        }
                    } else {
                        devicesState.add(new SimplePair<>(deviceProperties.getName(), "not existing"));
                    }
                }

            } else {
                addressedParcel.sendBack(new NotAuthorizedMessage());
            }
        });
    }

    private void send(ClientConnection connection, MessageData messageData) {
        connection.send(serializer.serialize(messageData));
    }
}
