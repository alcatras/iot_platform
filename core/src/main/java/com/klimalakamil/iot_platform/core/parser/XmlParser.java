package com.klimalakamil.iot_platform.core.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ekamkli on 2016-10-26.
 */
public abstract class XmlParser {

    Logger logger = Logger.getLogger(XmlParser.class.getName());

    public void parse(String filename) throws FileNotFoundException, ParseException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(filename);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to parse xml from file " + filename + ": " + e.getMessage(), e);
            throw new FileNotFoundException();
        }

        parseDocument(document.getDocumentElement());
    }

    public void parse(InputStream stream) throws IOException, ParserConfigurationException, SAXException, ParseException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(stream);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to parse xml from stream: " + e.getMessage(), e);
            throw e;
        }

        parseDocument(document.getDocumentElement());
    }

    protected abstract void parseDocument(Element root) throws ParseException;
}
