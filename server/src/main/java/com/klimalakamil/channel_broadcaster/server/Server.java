package com.klimalakamil.channel_broadcaster.server;


import com.klimalakamil.channel_broadcaster.core.ssl.SSLServerSettings;
import com.klimalakamil.channel_broadcaster.core.ssl.SSLServerThread;
import com.klimalakamil.channel_broadcaster.core.util.Log;
import com.klimalakamil.channel_broadcaster.server.parser.ServerSettings;

import javax.net.ssl.SSLSocket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.TimeZone;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Server extends SSLServerThread {

    protected Server(SSLServerSettings settings) {
        super(settings);
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        //System.setProperty("javax.net.debug", "all");

        Log.addOutput(new PrintStream(System.out), Log.Verbose.getLevel());
        Log.setTimeZone(TimeZone.getTimeZone("UCT"));

        ServerSettings settings = new ServerSettings();
        try {
            settings.parse(Server.class.getClassLoader().getResourceAsStream("server.xml"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            if (new File("server.xml").exists()) {
                settings.parse("server.xml");
            }
        } catch (ParseException e) {
            Log.Warning.l("Invalid server settings file: " + e.getMessage());
        }

        Server server = new Server(settings);

        (new Thread(server)).start();
    }

    @Override
    protected void setup() {

    }

    @Override
    protected void setupFailed(Exception e) {

    }

    @Override
    protected Runnable acceptConnection(final SSLSocket clientSocket) {
        return new ClientWorkerThread(clientSocket);
    }

    @Override
    protected void acceptConnectionFailed(Exception e) {

    }

    @Override
    protected void finish() {

    }
}
