package com.klimalakamil.channel_broadcaster.core.ssl;

import java.net.InetAddress;

/**
 * Created by ekamkli on 2016-11-20.
 */
public class SSLServerSettings {
    protected InetAddress inetAddress;
    protected int port;

    protected int backlogSize;
    protected int maxConnections;

    protected String clientPublicKeyStore;

    protected String serverPrivateKeyStore;
    protected char[] serverKeyStorePassword;

    public SSLServerSettings() {

    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBacklogSize() {
        return backlogSize;
    }

    public void setBacklogSize(int backlogSize) {
        this.backlogSize = backlogSize;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public String getClientPublicKeyStore() {
        return clientPublicKeyStore;
    }

    public void setClientPublicKeyStore(String clientPublicKeyStore) {
        this.clientPublicKeyStore = clientPublicKeyStore;
    }

    public String getServerPrivateKeyStore() {
        return serverPrivateKeyStore;
    }

    public void setServerPrivateKeyStore(String serverPrivateKeyStore) {
        this.serverPrivateKeyStore = serverPrivateKeyStore;
    }

    public char[] getServerKeyStorePassword() {
        return serverKeyStorePassword;
    }

    public void setServerKeyStorePassword(char[] serverKeyStorePassword) {
        this.serverKeyStorePassword = serverKeyStorePassword;
    }
}
