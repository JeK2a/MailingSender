package service;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

// Получение настроек из XML файла
public class SettingsMail {

    private static String url;
    private static String url_wss;

    public SettingsMail() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File("config_my.xml");
            doc = builder.parse(file);
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            System.err.println(ex);
        }

        assert doc != null;
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child instanceof Element) {
                Element childElement = (Element) child;
                Text textNode        = (Text) childElement.getFirstChild();
                String text          = textNode.getData().trim();

                switch (childElement.getTagName()) {
                    case "url":     url     = text; break;
                    case "url_wss": url_wss = text; break;
                }
            }
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUrlWSS() {
        return url_wss;
    }
}