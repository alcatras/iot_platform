package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.server.database.mappers.Mapper;
import message.messagedata.time.TimeRequest;
import message.messagedata.time.TimeResponse;

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
                addressedParcel.sendBack(new TimeResponse("NOT AUTHORIZED"));
            }
        });
    }
}
