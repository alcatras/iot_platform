package com.klimalakamil.iot_platform.api.client;

import com.klimalakamil.iot_platform.core.message.Parcel;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.ChannelParticipationRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.channel.NewChannelResponse;

/**
 * Created by kamil on 27.01.17.
 */
public interface ClientListener {

    void onConnectionClose();

    void onStatusMessage(GeneralStatusMessage generalStatusMessage);

    boolean acceptChannelRequest(ChannelParticipationRequest request);

    boolean acceptNewChannel(NewChannelResponse response);

    void parseMessage(Parcel parcel);
}
