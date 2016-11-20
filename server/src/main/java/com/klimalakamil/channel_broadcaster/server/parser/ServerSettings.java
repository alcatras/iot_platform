package com.klimalakamil.channel_broadcaster.server.parser;

import com.klimalakamil.channel_broadcaster.core.parser.XmlParser;
import com.klimalakamil.channel_broadcaster.core.ssl.SSLServerSettings;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * Created by ekamkli on 2016-11-19.
 */
public class ServerSettings extends SSLServerSettings {

    private XmlServerSettingsParser parser;

    public ServerSettings() {
        parser = new XmlServerSettingsParser();
    }

    public void parse(InputStream stream) throws ParserConfigurationException, SAXException, ParseException, IOException {
        parser.parse(stream);
    }

    public void parse(String filename) throws FileNotFoundException, ParseException {
        parser.parse(filename);
    }

    private class XmlServerSettingsParser extends XmlParser {
        public void parseDocument(Element root) throws ParseException {
            NodeList connectionSettings = ((Element) root.getElementsByTagName("connection").item(0)).getElementsByTagName("value");

            for (int i = 0; i < connectionSettings.getLength(); i++) {
                Element element = (Element) connectionSettings.item(i);

                String name = element.getAttribute("name");
                String value = element.getTextContent();

                if (name.equals("host")) {
                    try {
                        inetAddress = InetAddress.getByName(value);
                    } catch (UnknownHostException e) {
                        throw new ParseException("Unknown host: " + value, 0);
                    }
                } else if (name.equals("port")) {
                    port = Integer.parseInt(value);
                } else if (name.equals("backlog_size")) {
                    backlogSize = Integer.parseInt(value);
                } else if (name.equals("max_connections")) {
                    maxConnections = Integer.parseInt(value);
                }
            }

            NodeList securitySettings = ((Element) root.getElementsByTagName("security").item(0)).getElementsByTagName("value");

            for (int i = 0; i < securitySettings.getLength(); i++) {
                Element element = (Element) securitySettings.item(i);

                String name = element.getAttribute("name");
                String value = element.getTextContent();

                if (name.equals("server_private_key")) {
                    serverPrivateKeyStore = value;
                } else if (name.equals("server_private_key_password")) {
                    serverKeyStorePassword = value.toCharArray();
                } else if (name.equals("client_public_key")) {
                    clientPublicKeyStore = value;
                }
            }
        }
    }
}
