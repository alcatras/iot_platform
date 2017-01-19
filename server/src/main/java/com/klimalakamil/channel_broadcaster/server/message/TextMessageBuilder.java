package com.klimalakamil.channel_broadcaster.server.message;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import com.klimalakamil.channel_broadcaster.server.dispatcher.Dispatcher;
import message.Parcel;
import message.serializer.JsonSerializer;

import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

/**
 * Created by kamil on 18.01.17.
 */
public class TextMessageBuilder extends MessageBuilder<AddressedParcel> {

    private Logger logger = Logger.getLogger(TextMessageBuilder.class.getName());
    private ByteArrayOutputStream byteBuffer;

    private ClientConnection clientConnection;
    private JsonSerializer serializer;

    public TextMessageBuilder(ClientConnection connection, Dispatcher<AddressedParcel> dispatcher) {
        super(dispatcher);
        this.clientConnection = connection;

        byteBuffer = new ByteArrayOutputStream(2048);
        serializer = new JsonSerializer();
    }

    @Override
    public void receive(byte[] data, int length, boolean end) {
        byteBuffer.write(data, 0, length);

        if (end) {
            Parcel parcel = serializer.deserialize(byteBuffer.toByteArray());
            AddressedParcel addressedParcel = new AddressedParcel(parcel, clientConnection, serializer);
            dispatcher.dispatch(addressedParcel);
            byteBuffer.reset();
        }
    }
}