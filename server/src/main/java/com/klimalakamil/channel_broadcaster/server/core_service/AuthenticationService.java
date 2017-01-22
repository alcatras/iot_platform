package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.core.authentication.PasswordHelper;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.GeneralStatusMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.auth.LoginMessage;
import com.klimalakamil.channel_broadcaster.core.message.messagedata.auth.LogoutMessage;
import com.klimalakamil.channel_broadcaster.server.database.mappers.DeviceMapper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.MapperRegistry;
import com.klimalakamil.channel_broadcaster.server.database.mappers.SessionMapper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.UserMapper;
import com.klimalakamil.channel_broadcaster.server.database.models.Device;
import com.klimalakamil.channel_broadcaster.server.database.models.Session;
import com.klimalakamil.channel_broadcaster.server.database.models.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class AuthenticationService extends CoreService {

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
                    addressedParcel.sendBack(new GeneralStatusMessage(1, "Invalid device"));
                    logger.log(Level.INFO, "Failed login attempt: device do not exists: " + data.getDevice() + ", user: " + user);
                } else {
                    Session session = sessionMapper.get(device);
                    ClientConnection connection = addressedParcel.getConnection();
                    if (session == null) {
                        session = new Session(device, connection.getAddress(), connection.getPort(), LocalDateTime.now().plus(14, ChronoUnit.DAYS));
                        sessionMapper.insert(session);
                        addressedParcel.sendBack(new GeneralStatusMessage(0, "Logged in"));
                        logger.log(Level.INFO, "User logged in: " + user);
                    } else {
                        if (!session.getAddress().equals(connection.getAddress()) || session.getControlPort() != connection.getPort()) {
                            session.setAddress(connection.getAddress());
                            session.setControlPort(connection.getPort());
                            sessionMapper.update(session);
                            addressedParcel.sendBack(new GeneralStatusMessage(0, "Logged in"));
                            logger.log(Level.INFO, "User prolonged session from new address " + user);
                        } else {
                            addressedParcel.sendBack(new GeneralStatusMessage(1, "Already logged in"));
                            logger.log(Level.INFO, "Failed login attempt: already logged in: " + user);
                        }
                    }
                }
            } else {
                addressedParcel.sendBack(new GeneralStatusMessage(1, "Invalid username or password"));
                logger.log(Level.INFO, "Failed login attempt: invalid username or password " + user);
            }
        });

        addAction(LogoutMessage.class, addressedParcel -> {
            Session session = sessionMapper.get(addressedParcel.getConnection());

            if (session != null) {
                sessionMapper.delete(session);
                addressedParcel.sendBack(new GeneralStatusMessage(0, "Logged out"));
            } else {
                addressedParcel.sendBack(new GeneralStatusMessage(1, "Error occured"));
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

    public boolean isActive(ClientConnection connection) {
        return attemptSessionProlong(sessionMapper.get(connection));
    }

    public boolean isActive(Session session) {
        return attemptSessionProlong(session);
    }
}
