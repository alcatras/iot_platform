package com.klimalakamil.channel_broadcaster.server.core_service;

import com.google.gson.Gson;
import com.klimalakamil.channel_broadcaster.core.authentication.PasswordHelper;
import com.klimalakamil.channel_broadcaster.core.message.MessageDataWrapper;
import com.klimalakamil.channel_broadcaster.core.message.auth.LoginMessageData;
import com.klimalakamil.channel_broadcaster.server.database.mappers.MapperRegistry;
import com.klimalakamil.channel_broadcaster.server.database.mappers.UserMapper;
import com.klimalakamil.channel_broadcaster.server.database.models.User;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Created by kamil on 18.01.17.
 */
public class AuthenticationService extends CoreService {

    private UserMapper userMapper = (UserMapper) MapperRegistry.getInstance().forClass(User.class);
    private Map<String, Consumer<MessageDataWrapper>> actions;

    public AuthenticationService() {
        super(AuthenticationService.class);

        actions = new TreeMap<>();
        Gson gson = new Gson();

        actions.put(LoginMessageData.class.getCanonicalName(), wrapper -> {
            LoginMessageData data = gson.fromJson(new String(wrapper.data), LoginMessageData.class);

            User user = userMapper.get(data.login);

            System.out.println(user.getUsername());
            System.out.println(PasswordHelper.checkPassword(data.password.toCharArray(), user.getSalt(), user.getPasswordDigest()));

            System.out.println(data.password);
            System.out.println(data.deviceName);
        });
    }

    @Override
    public boolean parse(MessageDataWrapper message) {

        Consumer<MessageDataWrapper> consumer = actions.get(message.tag);
        if (consumer != null) {
            consumer.accept(message);
            return true;
        }
        return false;
    }
}
