package com.cosmic.snakegamecraft.logic;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HighscoreManager {
    private static final String FILE_PATH = "highscores.xml";

    public static void saveScore(String name, int score) {
        try {
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

            // Check if user exists
            NodeList nodes = root.getElementsByTagName("entry");
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
                root.appendChild(newEntry);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTopEntries(String currentUser, int limit) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return "";

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList nodes = doc.getElementsByTagName("entry");

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

        List<String> output = getStrings(currentUser, limit, entries);

        return String.join("\n", output);
    }

    @NotNull
    private static List<String> getStrings(String currentUser, int limit, List<Map.Entry<String, Integer>> entries) {
        List<String> output = new ArrayList<>();
        boolean userShown = false;

        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, Integer> e = entries.get(i);
            if (i < limit) {
                output.add((i + 1) + ". " + e.getKey() + " " + e.getValue());
            }
            if (e.getKey().equals(currentUser)) {
                userShown = true;
                if (i >= limit) {
                    output.add("...");
                    output.add((i + 1) + ". " + e.getKey() + " " + e.getValue());
                }
            }
        }
        return output;
    }
}