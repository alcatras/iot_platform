package com.klimalakamil.channel_broadcaster.server.message;

import com.klimalakamil.channel_broadcaster.core.connection.client.ClientConnection;
import message.MessageData;
import message.Parcel;
import message.serializer.JsonSerializer;

/**
 * Created by kamil on 19.01.17.
 */
public class AddressedParcel {

    private Parcel parcel;
    private ClientConnection connection;
    private JsonSerializer serializer;

    public AddressedParcel(Parcel parcel, ClientConnection connection, JsonSerializer serializer) {
        this.parcel = parcel;
        this.connection = connection;
        this.serializer = serializer;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public void setParcel(Parcel parcel) {
        this.parcel = parcel;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    public void sendBack(MessageData messageData) {
        connection.send(serializer.serialize(messageData));
    }
}
