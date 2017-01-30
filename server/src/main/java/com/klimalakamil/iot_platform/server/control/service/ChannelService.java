package com.klimalakamil.iot_platform.server.control.service;

import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelParticipationRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelResponse;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by kamil on 26.01.17.
 */
public class ChannelService extends Service {

    private AuthenticationService authenticationService = (AuthenticationService) ServiceRegistry.getInstance().get(
            AuthenticationService.class);
    private DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Device.class);
    private SessionMapper sessionMapper = (SessionMapper) MapperRegistry.getInstance().forClass(Session.class);
    private ConnectionRegistry connectionRegistry = ConnectionRegistry.getInstance();

    public ChannelService() {
        super(ChannelService.class);

        addAction(NewChannelRequest.class, addressedParcel -> {
            if(negotiateChannelCreation(addressedParcel)) {

            } else {

            }
        });
    }

    private boolean negotiateChannelCreation(AddressedParcel addressedParcel) {
        ClientWorker worker = addressedParcel.getWorker();
        Session clientSession = sessionMapper.get(worker);

        if (authenticationService.isActive(clientSession)) {
            NewChannelRequest channelRequest = addressedParcel.getMessageData(NewChannelRequest.class);
            Device rootDevice = clientSession.getDevice();

            DeviceProperties[] devices = channelRequest.getDevices();
            Map<String, DeviceState> states = Collections.synchronizedMap(new HashMap<>());

            ExpectedMessage expectedMessage = ExpectedMessage.getInstance();
            List<ExpectedMessage.ExMessageId> expectedMessages = new ArrayList<>();

            for (DeviceProperties deviceProperties : devices) {
                Device device = deviceMapper.get(rootDevice.getUser(), deviceProperties.getName());
                Session session = sessionMapper.get(device);

                if (device != null) {
                    if (session.isValid()) {
                        ClientWorker deviceWorker = connectionRegistry.get(
                                ClientContext.getUniqueId(session.getAddress(), session.getControlPort()));

                        if (deviceWorker != null) {
                            ChannelParticipationRequest request = new ChannelParticipationRequest(
                                    channelRequest.getName(), rootDevice.getName(), deviceProperties);

                            expectedMessages.add(expectedMessage.sendAndExpect(request, deviceWorker,
                                    new OnDeviceStateMessageListener(states, device.getName()), 15, TimeUnit.SECONDS));
                        } else {
                            states.put(deviceProperties.getName(), DeviceState.INACTIVE_DEVICE);
                        }
                    } else {
                        states.put(deviceProperties.getName(), DeviceState.INACTIVE_DEVICE);
                    }
                } else {
                    states.put(deviceProperties.getName(), DeviceState.INVALID_DEVICE);
                }
            }
            expectedMessage.waitUntilDone(expectedMessages);

            OnHostFinalDecisionListener listener = new OnHostFinalDecisionListener();
            expectedMessage.sendMessageAndWait(new NewChannelResponse(channelRequest.getName(), states), worker,
                    listener, 10, TimeUnit.SECONDS);

            return listener.isStatus();
        }
        return false;
    }

    class OnDeviceStateMessageListener implements ExpectedMessage.OnMessageReceivedListener {
        private Map<String, DeviceState> devices;
        private String name;

        public OnDeviceStateMessageListener(Map<String, DeviceState> devices, String name) {
            this.devices = devices;
            this.name = name;
        }

        @Override
        public void receive(AddressedParcel parcel) {
            if (parcel.getParcel().checkTag(GeneralStatusMessage.class)) {
                GeneralStatusMessage generalStatusMessage = parcel.getMessageData(GeneralStatusMessage.class);

                if (generalStatusMessage.checkCode(GeneralCodes.CHANNEL_ACCEPT)) {
                    devices.put(name, DeviceState.ACCEPTED);
                    return;
                }
            }
            devices.put(name, DeviceState.REFUSED);
        }

        @Override
        public void failed() {
            devices.put(name, DeviceState.TIME_OUT);
        }
    }

    class OnHostFinalDecisionListener implements ExpectedMessage.OnMessageReceivedListener {

        private boolean status = false;

        @Override
        public void receive(AddressedParcel parcel) {
            if (parcel.getParcel().checkTag(GeneralStatusMessage.class)) {
                GeneralStatusMessage generalStatusMessage = parcel.getMessageData(GeneralStatusMessage.class);

                if (generalStatusMessage.checkCode(GeneralCodes.CHANNEL_ACCEPT)) {
                    status = true;
                    return;
                }
            }
            status = false;
        }

        @Override
        public void failed() {
            status = false;
        }

        public boolean isStatus() {
            return status;
        }
    }
}
