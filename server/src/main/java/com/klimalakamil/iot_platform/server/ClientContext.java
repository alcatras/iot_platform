package com.klimalakamil.iot_platform.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by kamil on 26.01.17.
 */
public class ClientContext {

    public static final int CONTROL_PLANE_ID = 0;

    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private int id;

    public ClientContext(Socket socket) {
        this.socket = socket;
    }

    public static int getControlPlaneId() {
        return CONTROL_PLANE_ID;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUniqueId() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }
}
