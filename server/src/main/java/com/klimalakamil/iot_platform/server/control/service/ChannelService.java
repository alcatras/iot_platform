package com.klimalakamil.iot_platform.server.control.service;

import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.*;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceProperties;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceState;
import com.klimalakamil.iot_platform.server.ClientContext;
import com.klimalakamil.iot_platform.server.control.AddressedParcel;
import com.klimalakamil.iot_platform.server.control.ClientWorker;
import com.klimalakamil.iot_platform.server.control.ConnectionRegistry;
import com.klimalakamil.iot_platform.server.control.ExpectedMessage;
import com.klimalakamil.iot_platform.server.database.mappers.DeviceMapper;
import com.klimalakamil.iot_platform.server.database.mappers.MapperRegistry;
import com.klimalakamil.iot_platform.server.database.mappers.SessionMapper;
import com.klimalakamil.iot_platform.server.database.models.Device;
import com.klimalakamil.iot_platform.server.database.models.Session;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kamil on 26.01.17.
 */
public class ChannelService extends Service {

    private AuthenticationService authenticationService = (AuthenticationService) ServiceRegistry.getInstance().get(AuthenticationService.class);
    private DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Device.class);
    private SessionMapper sessionMapper = (SessionMapper) MapperRegistry.getInstance().forClass(Session.class);
    private ConnectionRegistry connectionRegistry = ConnectionRegistry.getInstance();

    public ChannelService() {
        super(ChannelService.class);

        addAction(NewChannelRequest.class, addressedParcel -> {
            // Phase one, ask parties about channel creation
            ClientWorker worker = addressedParcel.getWorker();
            Session clientSession = sessionMapper.get(worker);

            if (authenticationService.isActive(clientSession)) {
                long time = System.currentTimeMillis();

                NewChannelRequest channelRequest = addressedParcel.getMessageData(NewChannelRequest.class);
                Device rootDevice = clientSession.getDevice();

                DeviceProperties[] devices = channelRequest.getDevices();
                List<Long> ids = Collections.synchronizedList(new ArrayList<>());
                Map<String, DeviceState> devicesState = Collections.synchronizedMap(new HashMap<>());

                ExpectedMessage expectedMessage = ExpectedMessage.getInstance();

                for (DeviceProperties deviceProperties : devices) {
                    devicesState.put(deviceProperties.getName(), DeviceState.TIME_OUT);

                    Device device = deviceMapper.get(rootDevice.getUser(), deviceProperties.getName());
                    Session session = sessionMapper.get(device);

                    if (device != null) {
                        if (session.isValid()) {
                            ClientWorker deviceWorker = connectionRegistry.get(ClientContext.getUniqueId(session.getAddress(), session.getControlPort()));

                            if(deviceWorker != null) {
                                ChannelParticipationRequest request = new ChannelParticipationRequest(channelRequest.getName(), rootDevice.getName(), deviceProperties);

                                long id = ExpectedMessage.getId();
                                ids.add(id);

                                expectedMessage.expectMessage(id, deviceWorker, parcel -> {
                                    ids.remove(id);
                                    GeneralStatusMessage generalStatusMessage = parcel.getMessageData(GeneralStatusMessage.class);
                                    if(generalStatusMessage.getCode().equals(GeneralCodes.CHANNEL_ACCEPT)) {
                                        devicesState.put(deviceProperties.getName(), DeviceState.ACCEPTED);
                                    } else {
                                        devicesState.put(deviceProperties.getName(), DeviceState.REFUSED);
                                    }
                                });

                                deviceWorker.send(request, id);
                            } else {
                                devicesState.put(deviceProperties.getName(), DeviceState.INACTIVE_DEVICE);
                            }
                        } else {
                            devicesState.put(deviceProperties.getName(), DeviceState.INACTIVE_DEVICE);
                        }
                    } else {
                        devicesState.put(deviceProperties.getName(), DeviceState.INVALID_DEVICE);
                    }
                }

                while(System.currentTimeMillis() - time <= 10000) {
                    if(ids.size() == 0) {
                        worker.send(new NewChannelResponse(channelRequest.getName(), devicesState));
                        return;
                    }

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ignored) {}
                }

                for(long i: ids) {
                    expectedMessage.cancel(i);
                }

                long id = ExpectedMessage.getId();
                expectedMessage.expectMessage(id, worker, parcel -> {

                });

                worker.send(new NewChannelResponse(channelRequest.getName(), devicesState));

                // Phase two


            } else {
                addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.NOT_AUTHORIZED));
            }
        });
    }
}
