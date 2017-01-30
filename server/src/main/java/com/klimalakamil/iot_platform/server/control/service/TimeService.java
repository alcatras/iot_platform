package com.klimalakamil.iot_platform.server.control.service;

import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.time.TimeRequest;
import com.klimalakamil.iot_platform.core.message.messagedata.time.TimeResponse;
import com.klimalakamil.iot_platform.server.database.mappers.Mapper;

import java.time.LocalDateTime;

/**
 * Created by kamil on 26.01.17.
 */
public class TimeService extends Service {

    AuthenticationService authenticationService = (AuthenticationService) ServiceRegistry.getInstance().get(
            AuthenticationService.class);

    public TimeService() {
        super(TimeService.class);

        addAction(TimeRequest.class, addressedParcel -> {
            if (authenticationService.isActive(addressedParcel.getWorker())) {
                addressedParcel.sendBack(new TimeResponse(LocalDateTime.now().format(Mapper.formatter)));
            } else {
                addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.NOT_AUTHORIZED));
            }
        });
    }

}
