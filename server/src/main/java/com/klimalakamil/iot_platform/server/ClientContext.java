package com.klimalakamil.iot_platform.server;

import javax.net.ssl.SSLSocket;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by kamil on 26.01.17.
 */
public class ClientContext {

    public static final int CONTROL_PLANE_ID = 0;

    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private byte id;

    public ClientContext(Socket socket) {
        this.socket = socket;
    }

    public static int getControlPlaneId() {
        return CONTROL_PLANE_ID;
    }

    public static String getUniqueId(Socket socket) {
        return getUniqueId(socket.getInetAddress(), socket.getPort());
    }

    public static String getUniqueId(InetAddress address, int port) {
        return address.getHostAddress() + ":" + port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }
}
