package message.processors;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnectionListener;
import com.klimalakamil.channel_broadcaster.core.dispatcher.Dispatcher;

/**
 * Created by kamil on 18.01.17.
 */
public abstract class MessageBuilder<T> implements ClientConnectionListener {

    protected Dispatcher<T> dispatcher;

    public MessageBuilder(Dispatcher<T> dispatcher) {
        this.dispatcher = dispatcher;
    }
}
