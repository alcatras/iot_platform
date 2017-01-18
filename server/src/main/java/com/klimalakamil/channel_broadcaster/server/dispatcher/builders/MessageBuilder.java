package com.klimalakamil.channel_broadcaster.server.dispatcher.builders;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionListener;
import com.klimalakamil.channel_broadcaster.server.dispatcher.Dispatcher;
import com.klimalakamil.channel_broadcaster.server.dispatcher.message.Message;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class MessageBuilder<T extends Message> extends Dispatcher implements ClientConnectionListener {

}
