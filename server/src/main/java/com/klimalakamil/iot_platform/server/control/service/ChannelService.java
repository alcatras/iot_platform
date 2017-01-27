package com.klimalakamil.iot_platform.server.control.service;

import com.klimalakamil.iot_platform.server.control.ConnectionRegistry;
import com.klimalakamil.iot_platform.server.database.mappers.DeviceMapper;
import com.klimalakamil.iot_platform.server.database.mappers.MapperRegistry;
import com.klimalakamil.iot_platform.server.database.mappers.SessionMapper;
import com.klimalakamil.iot_platform.server.database.models.Device;
import com.klimalakamil.iot_platform.server.database.models.Session;

/**
 * Created by kamil on 26.01.17.
 */
public class ChannelService extends Service {

    private AuthenticationService authenticationService = (AuthenticationService) ServiceRegistry.getInstance().get(AuthenticationService.class);
    private DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Device.class);
    private SessionMapper sessionMapper = (SessionMapper) MapperRegistry.getInstance().forClass(Session.class);
    private ConnectionRegistry connectionRegistry = ConnectionRegistry.getInstance();

    //private Dispatcher<AddressedParcel> parcelDispatcher;

    public ChannelService(/*Dispatcher<AddressedParcel> parcelDispatcher*/) {
        super(ChannelService.class);

//        this.parcelDispatcher = parcelDispatcher;
//
//        addAction(NewChannelRequest.class, addressedParcel -> {
//            ClientWorker worker = addressedParcel.getWorker();
//            Session clientSession = sessionMapper.get(worker);
//
//            if (authenticationService.isActive(clientSession)) {
//                NewChannelRequest channelRequest = addressedParcel.getMessageData(NewChannelRequest.class);
//                Device rootDevice = clientSession.getDevice();
//
//                List<SimplePair<String, String>> devicesState = new ArrayList<>();
//
//                for (DeviceProperties deviceProperties : channelRequest.getDevices()) {
//                    Device device = deviceMapper.get(rootDevice.getUser(), deviceProperties.getName());
//                    Session session = sessionMapper.get(device);
//
//                    if (device != null) {
//                        if (session.isValid()) {
//                            ClientConnection deviceConnection = null;//connectionRegistry.get(session.getAddress(), session.getControlPort());
//                            ExpectedParcel expectedParcel = new ExpectedParcel(deviceConnection);
//                            parcelDispatcher.registerParser(expectedParcel);
//
//                            expectedParcel.addExpected(GeneralStatusMessage.class, parcel -> {
//                                System.out.println(parcel.getMessageData(GeneralStatusMessage.class));
//                            });
//
//                            ChannelParticipationRequest request = new ChannelParticipationRequest(rootDevice.getName(), deviceProperties);
//                            AddressedParcel response = expectedParcel.expectResponse(3, TimeUnit.SECONDS, request);
//
//                            if(response != null) {
//                                GeneralStatusMessage msg = response.getMessageData(GeneralStatusMessage.class);
//                                if(msg.getStatusId() == 0) {
//                                    devicesState.add(new SimplePair<>(deviceProperties.getName(), "ok"));
//                                } else {
//                                    devicesState.add(new SimplePair<>(deviceProperties.getName(), "refused"));
//                                }
//                            } else {
//                                devicesState.add(new SimplePair<>(deviceProperties.getName(), "timeout"));
//                            }
//
//                            parcelDispatcher.unregisterParser(expectedParcel);
//                        } else {
//                            devicesState.add(new SimplePair<>(deviceProperties.getName(), "not logged in"));
//                        }
//                    } else {
//                        devicesState.add(new SimplePair<>(deviceProperties.getName(), "not existing"));
//                    }
//                }
//
//                worker.send(new NewChannelResponse(devicesState));
//
//            } else {
//                addressedParcel.sendBack(new NotAuthorizedMessage());
//            }
//        });
    }
}
