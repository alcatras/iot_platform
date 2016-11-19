package com.klimalakamil.channel_broadcaster.client_sim;

import com.klimalakamil.channel_broadcaster.core.thread.SSLClientThread;
import org.apache.commons.cli.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Main {

    public static void main(String[] args) throws ParseException, UnknownHostException {
        Options options = new Options();

        options.addOption("h", "host", true, "Remote server host");
        options.addOption("p", "port", true, "Remote server port");

        options.addOption("c", "certificate", true, "Key store file");
        options.addOption("p", "password", true, "Key store password");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        String host = commandLine.getOptionValue("h");
        int port = Integer.parseInt(commandLine.getOptionValue("p"));

        String certificate = commandLine.getOptionValue("c");
        String password = commandLine.getOptionValue("p");

        if(host == null || port == 0 || certificate == null) {
            System.out.println("Invalid arguments");
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("client", options);
        } else {
            Client client = new Client(port, InetAddress.getByName(host), certificate, password);

            (new Thread(client)).start();
        }
    }
}
