package com.klimalakamil.channel_broadcaster.core.parser;

import com.klimalakamil.channel_broadcaster.core.util.Log;
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

/**
 * Created by ekamkli on 2016-10-26.
 */
public abstract class XmlParser {

    public void parse(String filename) throws FileNotFoundException, ParseException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(filename);
        } catch (Exception e) {
            Log.Error.l("Unable to parse xml from file " + filename + ": " + e.getMessage());
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
            Log.Error.l("Unable to parse xml from stream: " + e.getMessage());
            throw e;
        }

        parseDocument(document.getDocumentElement());
    }

    protected abstract void parseDocument(Element root) throws ParseException;
}
