package com.klimalakamil.channel_broadcaster.core.thread;

import java.net.InetAddress;

/**
 * Created by ekamkli on 2016-11-20.
 */
public class SSLClientSettings {

    private InetAddress inetAddress;
    private int port;

    private String serverPublicKeyStore;

    private String clientPrivateKeyStore;
    private char[] clientKeyStorePassword;

    public SSLClientSettings() {
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

    public String getServerPublicKeyStore() {
        return serverPublicKeyStore;
    }

    public void setServerPublicKeyStore(String serverPublicKeyStore) {
        this.serverPublicKeyStore = serverPublicKeyStore;
    }

    public String getClientPrivateKeyStore() {
        return clientPrivateKeyStore;
    }

    public void setClientPrivateKeyStore(String clientPrivateKeyStore) {
        this.clientPrivateKeyStore = clientPrivateKeyStore;
    }

    public char[] getClientKeyStorePassword() {
        return clientKeyStorePassword;
    }

    public void setClientKeyStorePassword(char[] clientKeyStorePassword) {
        this.clientKeyStorePassword = clientKeyStorePassword;
    }
}
