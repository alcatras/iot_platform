package com.klimalakamil.channel_broadcaster.server;


import com.klimalakamil.channel_broadcaster.core.thread.SSLServerThread;
import com.klimalakamil.channel_broadcaster.core.util.Log;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.TimeZone;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Server extends SSLServerThread {

    protected Server(int port, InetAddress inetAddress, int backlog, int maxThreads) {
        super(port, inetAddress, backlog, maxThreads);
    }

    public static void main(String[] args) throws InterruptedException {
        Log.addOutput(new PrintStream(System.out), Log.Verbose.getLevel());
        Log.setTimeZone(TimeZone.getTimeZone("UCT"));


    }

    @Override
    protected void setup() {

    }

    @Override
    protected void setupFailed(Exception e) {

    }

    @Override
    protected Runnable acceptConnection(Socket clientSocket) {
        return null;
    }

    @Override
    protected void acceptConnectionFailed(Exception e) {

    }

    @Override
    protected void finish() {

    }
}
