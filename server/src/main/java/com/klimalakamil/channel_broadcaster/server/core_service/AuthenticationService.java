package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.core.authentication.PasswordHelper;
import com.klimalakamil.channel_broadcaster.server.database.mappers.MapperRegistry;
import com.klimalakamil.channel_broadcaster.server.database.mappers.UserMapper;
import com.klimalakamil.channel_broadcaster.server.database.models.User;
import com.klimalakamil.channel_broadcaster.server.message.AddressedParcel;
import message.messagedata.GenericStatusMessage;
import message.messagedata.auth.LoginMessage;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Created by kamil on 18.01.17.
 */
public class AuthenticationService extends CoreService {

    private UserMapper userMapper = (UserMapper) MapperRegistry.getInstance().forClass(User.class);
    private Map<String, Consumer<AddressedParcel>> actions;

    public AuthenticationService() {
        super(AuthenticationService.class);

        actions = new TreeMap<>();

        actions.put(LoginMessage.class.getCanonicalName(), addressedParcel -> {
            LoginMessage data = addressedParcel.getParcel().getMessageData(LoginMessage.class);
            User user = userMapper.get(data.getUsername());

            String status = "Invalid username or password";

            if (user != null && PasswordHelper.checkPassword(data.getPassword().toCharArray(), user.getSalt(), user.getPasswordDigest())) {
                status = "OK";
            }
            addressedParcel.sendBack(new GenericStatusMessage(status.equals("OK") ? 0 : 1, status));
        });
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
