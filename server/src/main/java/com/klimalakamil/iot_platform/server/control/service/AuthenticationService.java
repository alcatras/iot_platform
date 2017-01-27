package com.klimalakamil.iot_platform.server.control.service;

import com.klimalakamil.iot_platform.core.authentication.PasswordHelper;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralCodes;
import com.klimalakamil.iot_platform.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.auth.LoginMessage;
import com.klimalakamil.iot_platform.core.message.messagedata.auth.LogoutMessage;
import com.klimalakamil.iot_platform.server.control.ClientWorker;
import com.klimalakamil.iot_platform.server.database.mappers.DeviceMapper;
import com.klimalakamil.iot_platform.server.database.mappers.MapperRegistry;
import com.klimalakamil.iot_platform.server.database.mappers.SessionMapper;
import com.klimalakamil.iot_platform.server.database.mappers.UserMapper;
import com.klimalakamil.iot_platform.server.database.models.Device;
import com.klimalakamil.iot_platform.server.database.models.Session;
import com.klimalakamil.iot_platform.server.database.models.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 26.01.17.
 */
public class AuthenticationService extends Service {

    private Logger logger = Logger.getLogger(AuthenticationService.class.getName());

    private UserMapper userMapper = (UserMapper) MapperRegistry.getInstance().forClass(User.class);
    private DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Device.class);
    private SessionMapper sessionMapper = (SessionMapper) MapperRegistry.getInstance().forClass(Session.class);

    public AuthenticationService() {
        super(AuthenticationService.class);

        addAction(LoginMessage.class, addressedParcel -> {
            LoginMessage data = addressedParcel.getParcel().getMessageData(LoginMessage.class);
            User user = userMapper.get(data.getUsername());

            if (user != null && PasswordHelper.checkPassword(data.getPassword().toCharArray(), user.getSalt(), user.getPasswordDigest())) {
                Device device = deviceMapper.get(user, data.getDevice());
                if (device == null) {
                    addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.INVALID_DEVICE));
                    logger.log(Level.INFO, "Failed login attempt: device do not exists: " + data.getDevice() + ", user: " + user);
                } else {
                    Session session = sessionMapper.get(device);
                    ClientWorker worker = addressedParcel.getWorker();
                    if (session == null) {
                        session = new Session(device, worker.getAddress(), worker.getPort(), LocalDateTime.now().plus(14, ChronoUnit.DAYS));
                        sessionMapper.insert(session);
                        addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.OK));
                        logger.log(Level.INFO, "User logged in: " + user);
                    } else {
                        if (!session.getAddress().equals(worker.getAddress()) || session.getControlPort() != worker.getPort()) {
                            session.setAddress(worker.getAddress());
                            session.setControlPort(worker.getPort());
                            sessionMapper.update(session);
                            addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.OK));
                            logger.log(Level.INFO, "User prolonged session from new address " + user);
                        } else {
                            addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.ALREADY_LOGGED_IN));
                            logger.log(Level.INFO, "Failed login attempt: already logged in: " + user);
                        }
                    }
                }
            } else {
                addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.INVALID_CREDENTIALS));
                logger.log(Level.INFO, "Failed login attempt: invalid username or password " + user);
            }
        });

        addAction(LogoutMessage.class, addressedParcel -> {
            Session session = sessionMapper.get(addressedParcel.getWorker());

            if (session != null) {
                sessionMapper.delete(session);
                addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.OK));
            } else {
                addressedParcel.sendBack(new GeneralStatusMessage(GeneralCodes.UNKNOWN_ERROR));
            }
        });
    }

    private boolean attemptSessionProlong(Session session) {
        if (session != null && session.isValid()) {
            session.setValidTo(LocalDateTime.now().plus(14, ChronoUnit.DAYS));
            return true;
        }
        return false;
    }

    public boolean isActive(Device device) {
        return attemptSessionProlong(sessionMapper.get(device));
    }

    public boolean isActive(ClientWorker worker) {
        return attemptSessionProlong(sessionMapper.get(worker));
    }

    public boolean isActive(Session session) {
        return attemptSessionProlong(session);
    }
}
