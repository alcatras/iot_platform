package com.klimalakamil.iot_platform.server.core_service;

import com.klimalakamil.iot_platform.core.connection.client.ClientConnection;
import com.klimalakamil.iot_platform.core.dispatcher.Dispatcher;
import com.klimalakamil.iot_platform.core.dispatcher.message.ExpectedParcel;
import com.klimalakamil.iot_platform.core.message.AddressedParcel;
import com.klimalakamil.iot_platform.core.message.MessageData;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.NotAuthorizedMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.*;
import com.klimalakamil.iot_platform.core.message.serializer.JsonSerializer;
import com.klimalakamil.iot_platform.server.ConnectionRegistry;
import com.klimalakamil.iot_platform.server.database.mappers.DeviceMapper;
import com.klimalakamil.iot_platform.server.database.mappers.MapperRegistry;
import com.klimalakamil.iot_platform.server.database.mappers.SessionMapper;
import com.klimalakamil.iot_platform.server.database.models.Device;
import com.klimalakamil.iot_platform.server.database.models.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kamil on 22.01.17.
 */
public class ChannelService extends CoreService {

    private AuthenticationService authenticationService = (AuthenticationService) CoreServiceRegistry.getInstance().get(AuthenticationService.class);
    private DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Device.class);
    private SessionMapper sessionMapper = (SessionMapper) MapperRegistry.getInstance().forClass(Session.class);
    private ConnectionRegistry connectionRegistry = ConnectionRegistry.getInstance();
    private JsonSerializer serializer = new JsonSerializer();

    private Dispatcher<AddressedParcel> parcelDispatcher;

    public ChannelService(Dispatcher<AddressedParcel> parcelDispatcher) {
        super(ChannelService.class);

        this.parcelDispatcher = parcelDispatcher;

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
                            ExpectedParcel expectedParcel = new ExpectedParcel(deviceConnection);
                            parcelDispatcher.registerParser(expectedParcel);

                            expectedParcel.addExpected(GeneralStatusMessage.class, parcel -> {
                                System.out.println(parcel.getMessageData(GeneralStatusMessage.class));
                            });

                            ChannelParticipationRequest request = new ChannelParticipationRequest(rootDevice.getName(), deviceProperties);
                            AddressedParcel response = expectedParcel.expectResponse(3, TimeUnit.SECONDS, request);

                            if(response != null) {
                                GeneralStatusMessage msg = response.getMessageData(GeneralStatusMessage.class);
                                if(msg.getStatusId() == 0) {
                                    devicesState.add(new SimplePair<>(deviceProperties.getName(), "ok"));
                                } else {
                                    devicesState.add(new SimplePair<>(deviceProperties.getName(), "refused"));
                                }
                            } else {
                                devicesState.add(new SimplePair<>(deviceProperties.getName(), "timeout"));
                            }

                            parcelDispatcher.unregisterParser(expectedParcel);
                        } else {
                            devicesState.add(new SimplePair<>(deviceProperties.getName(), "not logged in"));
                        }
                    } else {
                        devicesState.add(new SimplePair<>(deviceProperties.getName(), "not existing"));
                    }
                }

                send(connection, new NewChannelResponse(devicesState));

            } else {
                addressedParcel.sendBack(new NotAuthorizedMessage());
            }
        });
    }

    private void send(ClientConnection connection, MessageData messageData) {
        connection.send(serializer.serialize(messageData));
    }
}
