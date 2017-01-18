package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.server.database.mappers.MapperRegistry;
import com.klimalakamil.channel_broadcaster.server.database.mappers.UserMapper;
import com.klimalakamil.channel_broadcaster.server.database.models.User;

/**
 * Created by kamil on 18.01.17.
 */
public class AuthenticationService extends CoreService {

    private UserMapper userMapper = (UserMapper) MapperRegistry.getInstance().forClass(User.class);

    public AuthenticationService() {
        super(AuthenticationService.class);
    }

    @Override
    public boolean parse(String message) {
        System.out.println(message);
        return true;
    }
}
