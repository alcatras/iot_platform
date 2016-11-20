package com.klimalakamil.channel_broadcaster.client_sim;

import com.klimalakamil.channel_broadcaster.core.thread.SSLClientThread;
import com.klimalakamil.channel_broadcaster.core.util.Log;

import java.io.*;
import java.net.InetAddress;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Client extends SSLClientThread {

    public Client(int port, InetAddress inetAddress) {
        super(port, inetAddress);
    }

    @Override
    protected void setup() {

    }

    @Override
    protected void setupFailed(Exception e) {
        Log.Error.l("Failed to setup client connection: " + e.getMessage());
        e.printStackTrace();
    }

    @Override
    protected void loop(InputStream inputStream, OutputStream outputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(true) {
            try {
                System.out.println(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
