package com.cosmic.snakegamecraft.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.cosmic.snakegamecraft.ui.GameSettings;
import org.w3c.dom.*;

import java.io.File;

public class SettingsLoader {
    private static final String FILE_PATH = "settings.xml";

    public static GameSettings loadSettings(String username) {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return new GameSettings(1.0);


            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList users = doc.getElementsByTagName("user");
            for (int i = 0; i < users.getLength(); i++) {
                Element user = (Element) users.item(i);
                if (user.getAttribute("name").equals(username)) {
                    double speed = Double.parseDouble(user.getAttribute("speed"));
                    boolean canModernMode = Boolean.parseBoolean(user.getAttribute("canModernMode"));
                    boolean canCrazyMode = Boolean.parseBoolean(user.getAttribute("canCrazyMode"));

                    return new GameSettings(speed, canModernMode, canCrazyMode) ;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new GameSettings(1.0);
    }

    public static void saveSettings(GameSettings settings, String username) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc;
            Element root;

            File file = new File(FILE_PATH);
            if (file.exists()) {
                doc = builder.parse(file);
                root = doc.getDocumentElement();
            } else {
                doc = builder.newDocument();
                root = doc.createElement("settings");
                doc.appendChild(root);
            }

            NodeList users = root.getElementsByTagName("user");
            Element userElem = null;

            for (int i = 0; i < users.getLength(); i++) {
                Element e = (Element) users.item(i);
                if (e.getAttribute("name").equals(username)) {
                    userElem = e;
                    break;
                }
            }

            if (userElem == null) {
                userElem = doc.createElement("user");
                userElem.setAttribute("name", username);
                root.appendChild(userElem);
            }

            userElem.setAttribute("speed", String.valueOf(settings.speedMultiplier()));
            userElem.setAttribute("canModernMode", String.valueOf(settings.canModernMode()));
            userElem.setAttribute("canCrazyMode", String.valueOf(settings.canCrazyMode()));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}