package com.klimalakamil.iot_platform.server.control;

import com.klimalakamil.iot_platform.server.control.service.AuthenticationService;
import com.klimalakamil.iot_platform.server.control.service.TimeService;
import com.klimalakamil.iot_platform.server.generic.Parser;
import com.klimalakamil.iot_platform.server.ClientContext;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class ControlConnectionHandler implements Parser<ClientContext> {

    private Logger logger = Logger.getLogger(ControlConnectionHandler.class.getName());

    private Executor executor;
    private MessageDispatcher messageDispatcher;

    public ControlConnectionHandler() {
        executor = Executors.newWorkStealingPool();
        messageDispatcher = new MessageDispatcher(5);

        // Create core services
        AuthenticationService authenticationService = new AuthenticationService();
        messageDispatcher.registerParser(authenticationService);

        TimeService timeService = new TimeService();
        messageDispatcher.registerParser(timeService);
//
//        ChannelService channelService = new ChannelService(controlDispatcher);
//        messageDispatcher.registerParser(channelService);
    }

    @Override
    public boolean parse(ClientContext data) {
        if(data.getId() == ClientContext.CONTROL_PLANE_ID) {
            ClientWorker clientWorker = new ClientWorker(data, messageDispatcher);
            executor.execute(clientWorker);
            return true;
        }
        return false;
    }
}
