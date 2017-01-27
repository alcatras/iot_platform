package com.klimalakamil.iot_platform.core.message.processors;

import com.klimalakamil.iot_platform.core.connection.client.BytePacket;
import com.klimalakamil.iot_platform.core.connection.client.ClientConnection;
import com.klimalakamil.iot_platform.core.dispatcher.Dispatcher;
import com.klimalakamil.iot_platform.core.message.Parcel;
import com.klimalakamil.iot_platform.core.message.serializer.JsonSerializer;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
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
    public boolean parse(BytePacket message) {
        byteBuffer.write(message.data, 0, message.length);

        if (message.end) {
            Parcel parcel = serializer.deserialize(byteBuffer.toByteArray());
            AddressedParcel addressedParcel = new AddressedParcel(parcel, clientConnection, serializer);
            logger.log(Level.INFO, "Dispatching new message with tag: " + parcel.getTag() + " " + parcel.dump());
            dispatcher.dispatch(addressedParcel);
            byteBuffer.reset();
        }
        return true;
    }
}