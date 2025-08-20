package com.cosmic.snakegamecraft.logic;

import com.cosmic.snakegamecraft.ui.GameMode;
import com.cosmic.snakegamecraft.util.XMLEmptyLines;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;

public class HighscoreManager {
    private static final String FILE_PATH = "highscores.xml";

    public static void saveScore(GameMode gameMode, String name, int score) {
        try {
            if ("guest".equalsIgnoreCase(name)) return;

            File file = new File(FILE_PATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            Element root;

            if (file.exists()) {
                doc = dBuilder.parse(file);
                root = doc.getDocumentElement();
            } else {
                doc = dBuilder.newDocument();
                root = doc.createElement("highscores");
                doc.appendChild(root);
            }

            // Find or create game mode node
            Element modeElement = getOrCreateModeElement(doc, root, gameMode.name());

            // Update or add player score
            NodeList nodes = modeElement.getElementsByTagName("entry");
            boolean updated = false;

            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                if (e.getAttribute("name").equals(name)) {
                    int existingScore = Integer.parseInt(e.getAttribute("score"));
                    if (score > existingScore) {
                        e.setAttribute("score", String.valueOf(score));
                    }
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                Element newEntry = doc.createElement("entry");
                newEntry.setAttribute("name", name);
                newEntry.setAttribute("score", String.valueOf(score));
                modeElement.appendChild(newEntry);
            }

            // Save XML
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(file));

            XMLEmptyLines.removeEmptyLinesFromXml(FILE_PATH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Text> getTopEntries(GameMode gameMode, String currentUser, int limit) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return Collections.emptyList();

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            Element modeElement = getModeElement(doc, gameMode.name());
            if (modeElement == null){

                System.out.println("Empty Collection");
                return Collections.emptyList();
            }

            NodeList nodes = modeElement.getElementsByTagName("entry");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                entries.add(Map.entry(
                        e.getAttribute("name"),
                        Integer.parseInt(e.getAttribute("score"))
                ));
            }

            entries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buildTextList(currentUser, limit, entries);
    }

    public static int getCurrentUserScore(GameMode gameMode, String username) {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return -1;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            Element modeElement = getModeElement(doc, gameMode.name());
            if (modeElement == null) return 0;

            NodeList nodes = modeElement.getElementsByTagName("entry");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                if (e.getAttribute("name").equals(username)) {
                    return Integer.parseInt(e.getAttribute("score"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static Element getOrCreateModeElement(Document doc, Element root, String modeName) {
        Element modeElement = getModeElement(doc, modeName);
        if (modeElement == null) {
            modeElement = doc.createElement(modeName);
            root.appendChild(modeElement);
        }
        return modeElement;
    }

    private static Element getModeElement(Document doc, String modeName) {
        NodeList modeNodes = doc.getElementsByTagName(modeName);
        return modeNodes.getLength() > 0 ? (Element) modeNodes.item(0) : null;
    }

    @NotNull
    private static List<Text> buildTextList(String currentUser, int limit, List<Map.Entry<String, Integer>> entries) {
        List<Text> output = new ArrayList<>();
        boolean currentUserAlreadyShown = false;

        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, Integer> e = entries.get(i);

            if (i < limit || (e.getKey().equals(currentUser) && !currentUserAlreadyShown)) {
                if (i == limit) {
                    Text dots = new Text("...\n");
                    dots.setFill(Color.BLACK);
                    output.add(dots);
                }

                Text line = new Text((i + 1) + ". " + e.getKey() + " " + e.getValue() + "\n");

                if (e.getKey().equals(currentUser)) {
                    line.setFill(Color.GOLD);
                    line.setFont(Font.font("System", FontWeight.BOLD, 16));
                    currentUserAlreadyShown = true;
                } else {
                    line.setFill(Color.BLACK);
                    line.setFont(Font.font("System", FontWeight.NORMAL, 16));
                }

                output.add(line);
            }
        }
        return output;
    }
}

