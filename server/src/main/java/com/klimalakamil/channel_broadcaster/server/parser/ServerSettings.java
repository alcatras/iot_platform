package com.klimalakamil.channel_broadcaster.server.parser;

import com.klimalakamil.channel_broadcaster.core.parser.XmlParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class ServerSettings extends XmlParser {

    private InetAddress inetAddress;
    private int port;

    private int backlogSize;
    private int maxConnections;
    private String keyStore;

    @Override
    protected void parseDocument(Element root) throws ParseException {
        NodeList connectionSettings = ((Element) root.getElementsByTagName("connection").item(0)).getElementsByTagName("value");


        for (int i = 0; i < connectionSettings.getLength(); i++) {
            Element element = (Element) connectionSettings.item(i);

            String name = element.getAttribute("name");
            String value = element.getTextContent();

            if(name.equals("host")) {
                try {
                    inetAddress = InetAddress.getByName(value);
                } catch (UnknownHostException e) {
                    throw new ParseException("Unknown host: " + value, 0);
                }
            } else if(name.equals("port")) {
                port = Integer.parseInt(value);
            } else if(name.equals("backlog_size")) {
                backlogSize = Integer.parseInt(value);
            } else if(name.equals("max_connections")) {
                maxConnections = Integer.parseInt(value);
            }
        }

        NodeList securitySettings = ((Element) root.getElementsByTagName("security").item(0)).getElementsByTagName("value");

        for (int i = 0; i < securitySettings.getLength(); i++) {
            Element element = (Element) securitySettings.item(i);

            String name = element.getAttribute("name");
            String value = element.getTextContent();

            if(name.equals("key_store")) {
                keyStore = value;
            }
        }
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    public int getBacklogSize() {
        return backlogSize;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getKeyStore() {
        return keyStore;
    }
}
