package com.klimalakamil.channel_broadcaster.core.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;

/**
 * Created by ekamkli on 2016-10-26.
 */
public abstract class XmlParser {

    public XmlParser() {
    }

    public void parse(String filename) throws FileNotFoundException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(filename);
        } catch (Exception e) {
            throw new FileNotFoundException();
        }
        
        parseDocument(document.getDocumentElement());
    }

    protected abstract void parseDocument(Element root);
}
