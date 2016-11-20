package com.klimalakamil.channel_broadcaster.server;


import com.klimalakamil.channel_broadcaster.core.thread.SSLServerThread;
import com.klimalakamil.channel_broadcaster.core.util.Log;
import com.klimalakamil.channel_broadcaster.server.parser.ServerSettings;
import org.xml.sax.SAXException;

import javax.net.ssl.SSLSocket;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.TimeZone;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Server extends SSLServerThread {

    protected Server(int port, InetAddress inetAddress, int backlog, int maxThreads) {
        super(port, inetAddress, backlog, maxThreads);
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
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
            if(new File("server.xml").exists()) {
                settings.parse("server.xml");
            }
        } catch (ParseException e) {
            Log.Warning.l("Invalid server settings file: " + e.getMessage());
        }

        System.setProperty("javax.net.ssl.keyStore", "server.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "sOQe3ZiR");

        Server server = new Server(settings.getPort(), settings.getInetAddress(), settings.getBacklogSize(), settings.getMaxConnections());

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
