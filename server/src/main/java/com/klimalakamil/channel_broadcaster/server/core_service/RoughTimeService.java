package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.core.message.messagedata.NotAuthorizedMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.time.TimeRequest;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.time.TimeResponse;
import com.klimalakamil.channel_broadcaster.server.database.mappers.Mapper;

import java.time.LocalDateTime;

/**
 * Created by kamil on 22.01.17.
 */
public class RoughTimeService extends CoreService {
    AuthenticationService authenticationService = (AuthenticationService) CoreServiceRegistry.getInstance().get(AuthenticationService.class);

    public RoughTimeService() {
        super(RoughTimeService.class);

        addAction(TimeRequest.class, addressedParcel -> {
            if (authenticationService.isActive(addressedParcel.getConnection())) {
                addressedParcel.sendBack(new TimeResponse(LocalDateTime.now().format(Mapper.formatter)));
            } else {
                addressedParcel.sendBack(new NotAuthorizedMessage());
            }
        });
    }
}
