package com.klimalakamil.channel_broadcaster.server.core_service;

import com.klimalakamil.channel_broadcaster.core.authentication.PasswordHelper;
import com.klimalakamil.channel_broadcaster.core.message.TextMessageBuilder;
import com.klimalakamil.channel_broadcaster.core.message.auth.LoginMsgData;
import com.klimalakamil.channel_broadcaster.core.message.auth.LoginResponseMsgData;
import com.klimalakamil.channel_broadcaster.server.database.mappers.MapperRegistry;
import com.klimalakamil.channel_broadcaster.server.database.mappers.UserMapper;
import com.klimalakamil.channel_broadcaster.server.database.models.User;
import com.klimalakamil.channel_broadcaster.server.message.MessageContext;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Created by kamil on 18.01.17.
 */
public class AuthenticationService extends CoreService {

    private UserMapper userMapper = (UserMapper) MapperRegistry.getInstance().forClass(User.class);
    private Map<String, Consumer<MessageContext>> actions;

    public AuthenticationService() {
        super(AuthenticationService.class);

        actions = new TreeMap<>();

        actions.put(LoginMsgData.class.getCanonicalName(), ctxt -> {
            LoginMsgData data = ctxt.wrapper.getContent(LoginMsgData.class);
            User user = userMapper.get(data.login);

            String status = "Invalid username or password";

            if (user != null && PasswordHelper.checkPassword(data.password.toCharArray(), user.getSalt(), user.getPasswordDigest())) {
                status = "OK";

            }

            ctxt.connection.send(new TextMessageBuilder()
                    .setTag(LoginResponseMsgData.class.getCanonicalName())
                    .setMessageData(new LoginResponseMsgData(status))
                    .getSerialized());
        });
    }

    @Override
    public boolean parse(MessageContext ctxt) {

        Consumer<MessageContext> consumer = actions.get(ctxt.wrapper.tag);
        if (consumer != null) {
            consumer.accept(ctxt);
            return true;
        }
        return false;
    }
}
