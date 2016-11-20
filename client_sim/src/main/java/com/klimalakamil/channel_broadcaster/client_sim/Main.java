package com.klimalakamil.channel_broadcaster.client_sim;

import com.klimalakamil.channel_broadcaster.api.client.Client;
import com.klimalakamil.channel_broadcaster.core.thread.SSLClientSettings;
import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class Main {

    public static void main(String[] args) throws ParseException, UnknownHostException {
        System.setProperty("javax.net.debug", "all");

        Options options = new Options();

        options.addOption("h", "host", true, "Remote server host");
        options.addOption("p", "port", true, "Remote server port");

        options.addOption("x", "password", true, "Key store password");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        String host = commandLine.getOptionValue("h");
        int port = Integer.parseInt(commandLine.getOptionValue("p"));

        String password = commandLine.getOptionValue("x");

        if (host == null || port == 0 || password == null) {
            System.out.println("Invalid arguments");
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("client", options);
        } else {
            SSLClientSettings settings = new SSLClientSettings();
            settings.setClientKeyStorePassword(password.toCharArray());
            settings.setClientPrivateKeyStore("client.private");
            settings.setInetAddress(InetAddress.getByName(host));
            settings.setPort(port);
            settings.setServerPublicKeyStore("server.public");

            Client client = new Client(settings);
        }
    }
}
