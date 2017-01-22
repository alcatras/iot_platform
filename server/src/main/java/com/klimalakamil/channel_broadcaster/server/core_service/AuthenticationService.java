package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.core.authentication.PasswordHelper;
import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.server.database.mappers.DeviceMapper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.MapperRegistry;
import com.klimalakamil.channel_broadcaster.server.database.mappers.SessionMapper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.UserMapper;
import com.klimalakamil.channel_broadcaster.server.database.models.Device;
import com.klimalakamil.channel_broadcaster.server.database.models.Session;
import com.klimalakamil.channel_broadcaster.server.database.models.User;
import message.AddressedParcel;
import message.messagedata.GeneralStatusMessage;
import message.messagedata.auth.LoginMessage;
import message.messagedata.auth.LogoutMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Created by kamil on 18.01.17.
 */
public class AuthenticationService extends CoreService {

    private UserMapper userMapper = (UserMapper) MapperRegistry.getInstance().forClass(User.class);
    private DeviceMapper deviceMapper = (DeviceMapper) MapperRegistry.getInstance().forClass(Device.class);
    private SessionMapper sessionMapper = (SessionMapper) MapperRegistry.getInstance().forClass(Session.class);

    private Map<String, Consumer<AddressedParcel>> actions;

    public AuthenticationService() {
        super(AuthenticationService.class);

        actions = new TreeMap<>();

        actions.put(LoginMessage.class.getCanonicalName(), addressedParcel -> {
            LoginMessage data = addressedParcel.getParcel().getMessageData(LoginMessage.class);
            User user = userMapper.get(data.getUsername());

            if (user != null && PasswordHelper.checkPassword(data.getPassword().toCharArray(), user.getSalt(), user.getPasswordDigest())) {
                Device device = deviceMapper.get(user, data.getDevice());
                if (device == null) {
                    addressedParcel.sendBack(new GeneralStatusMessage(1, "Invalid device"));
                } else {
                    Session session = sessionMapper.get(device);
                    ClientConnection connection = addressedParcel.getConnection();
                    if (session == null) {
                        session = new Session(device, connection.getAddress(), connection.getPort(), LocalDateTime.now().plus(14, ChronoUnit.DAYS));
                        sessionMapper.insert(session);
                        addressedParcel.sendBack(new GeneralStatusMessage(0, "Logged in"));
                    } else {
                        if (!session.getAddress().equals(connection.getAddress()) || session.getControlPort() != connection.getPort()) {
                            session.setAddress(connection.getAddress());
                            session.setControlPort(connection.getPort());
                            sessionMapper.update(session);
                            addressedParcel.sendBack(new GeneralStatusMessage(0, "Logged in"));
                        } else {
                            addressedParcel.sendBack(new GeneralStatusMessage(1, "Already logged in"));
                        }
                    }
                }
            } else {
                addressedParcel.sendBack(new GeneralStatusMessage(1, "Invalid username or password"));
            }
        });

        actions.put(LogoutMessage.class.getCanonicalName(), addressedParcel -> {
            Session session = sessionMapper.get(addressedParcel.getConnection());

            if (session != null) {
                sessionMapper.delete(session);
                addressedParcel.sendBack(new GeneralStatusMessage(0, "Logged out"));
            } else {
                addressedParcel.sendBack(new GeneralStatusMessage(1, "Error occured"));
            }
        });
    }

    public boolean isActive(Device device) {
        Session session = sessionMapper.get(device);
        return session != null && session.getValidTo().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean parse(AddressedParcel addressedParcel) {

        Consumer<AddressedParcel> consumer = actions.get(addressedParcel.getParcel().getTag());
        if (consumer != null) {
            consumer.accept(addressedParcel);
            return true;
        }
        return false;
    }
}
