package com.klimalakamil.iot_platform.server.control.service;

import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelParticipationRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelResponse;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceProperties;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.util.DeviceState;
import com.klimalakamil.iot_platform.server.ClientContext;
import com.klimalakamil.iot_platform.server.channel.ChannelDeviceInfo;
import com.klimalakamil.iot_platform.server.channel.ChannelManager;
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
    private ChannelManager channelManager = ChannelManager.getInstance();

    public ChannelService() {
        super(ChannelService.class);

        addAction(NewChannelRequest.class, addressedParcel -> {
            ClientWorker worker = addressedParcel.getWorker();
            Session clientSession = sessionMapper.get(worker);

            if (authenticationService.isActive(clientSession)) {
                NewChannelRequest channelRequest = addressedParcel.getMessageData(NewChannelRequest.class);
                Device rootDevice = clientSession.getDevice();

                Map<String, ChannelDeviceInfo> devices = new HashMap<>();
                for(DeviceProperties deviceProperties: channelRequest.getDevices()) {
                    ClientWorker deviceWorker = null;
                    Device device = deviceMapper.get(rootDevice.getUser(), deviceProperties.getName());

                    if(device != null) {
                        Session session = sessionMapper.get(device);
                        if(session.isValid()) {
                            deviceWorker = connectionRegistry.get(
                                    ClientContext.getUniqueId(session.getAddress(), session.getControlPort()));
                        }
                    }
                    devices.put(deviceProperties.getName(), new ChannelDeviceInfo(deviceProperties, deviceWorker));
                }

                ExpectedMessage expectedMessage = ExpectedMessage.getInstance();
                List<ExpectedMessage.ExMessageId> expectedMessages = new ArrayList<>();

                for (Map.Entry<String, ChannelDeviceInfo> deviceInfoSet : devices.entrySet()) {
                    String name = deviceInfoSet.getKey();
                    ChannelDeviceInfo channelDeviceInfo = deviceInfoSet.getValue();

                    if(channelDeviceInfo.getWorker() != null) {
                        ChannelParticipationRequest request = new ChannelParticipationRequest(channelDeviceInfo.getName(), rootDevice.getName(), channelDeviceInfo.isCanRead(), channelDeviceInfo.isCanWrite());

                        expectedMessages.add(expectedMessage.sendAndExpect(request, channelDeviceInfo.getWorker(), new OnDeviceStateMessageListener(devices, name), 15, TimeUnit.SECONDS));
                    } else {
                        devices.get(name).setState(DeviceState.INACTIVE_DEVICE);
                    }
                }
                expectedMessage.waitUntilDone(expectedMessages);

                OnHostFinalDecisionListener listener = new OnHostFinalDecisionListener();
                Map<String, DeviceState> states = new HashMap<>();
                for(ChannelDeviceInfo channelDeviceInfo : devices.values()) {
                    states.put(channelDeviceInfo.getName(), channelDeviceInfo.getState());
                }
                expectedMessage.sendMessageAndWait(new NewChannelResponse(channelRequest.getName(), states), worker,
                        listener, 10, TimeUnit.SECONDS);

                if(listener.isAccepted()) {
                    channelManager.createChannel(channelRequest.getName(), new ArrayList<>(devices.values()));
                } else {
                    for(ChannelDeviceInfo channelDeviceInfo : devices.values()) {
                        if(channelDeviceInfo.getState().equals(DeviceState.ACCEPTED)) {
                            channelDeviceInfo.getWorker().send(new GeneralStatusMessage(GeneralCodes.CHANNEL_ABORTED));
                        }
                    }
                }
            }
        });
    }

    class OnDeviceStateMessageListener implements ExpectedMessage.OnMessageReceivedListener {
        private Map<String, ChannelDeviceInfo> devices;
        private String name;

        public OnDeviceStateMessageListener(Map<String, ChannelDeviceInfo> devices, String name) {
            this.devices = devices;
            this.name = name;
        }

        @Override
        public void receive(AddressedParcel parcel) {
            if (parcel.getParcel().checkTag(GeneralStatusMessage.class)) {
                GeneralStatusMessage generalStatusMessage = parcel.getMessageData(GeneralStatusMessage.class);

                if (generalStatusMessage.checkCode(GeneralCodes.CHANNEL_ACCEPT)) {
                    devices.get(name).setState(DeviceState.ACCEPTED);
                    return;
                }
            }
            devices.get(name).setState(DeviceState.REFUSED);
        }

        @Override
        public void failed() {
            devices.get(name).setState(DeviceState.TIME_OUT);
        }
    }

    class OnHostFinalDecisionListener implements ExpectedMessage.OnMessageReceivedListener {

        private boolean accepted = false;

        @Override
        public void receive(AddressedParcel parcel) {
            if (parcel.getParcel().checkTag(GeneralStatusMessage.class)) {
                GeneralStatusMessage generalStatusMessage = parcel.getMessageData(GeneralStatusMessage.class);

                if (generalStatusMessage.checkCode(GeneralCodes.CHANNEL_ACCEPT)) {
                    accepted = true;
                    return;
                }
            }
            accepted = false;
        }

        @Override
        public void failed() {
            accepted = false;
        }

        public boolean isAccepted() {
            return accepted;
        }
    }
}
