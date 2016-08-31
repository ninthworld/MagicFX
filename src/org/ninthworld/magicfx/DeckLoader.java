package org.ninthworld.magicfx;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class DeckLoader {

    public static DeckData loadDeckFile(File file, HashMap<String, CardData> allCards){
        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
        switch(ext){
            case "dec":
                return openDec(file, allCards);
            case "deck":
                return openDeck(file, allCards);
            case "jdeck":
                return openJDeck(file, allCards);
            default:
                return new DeckData();
        }
    }

    private static DeckData openDec(File file, HashMap<String, CardData> allCards){
        DeckData deckData = new DeckData();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean sb = false;
            while((line = br.readLine()) != null){
                if(line.startsWith("//")){
                    sb = true;
                }else{
                    if(sb){
                        line = line.replaceAll("SB: ", "");
                    }

                    String count = line.substring(0, line.indexOf(" "));
                    String name = line.substring(line.indexOf(" ")+1);

                    CardData card = null;
                    for(CardData cardData : allCards.values()){
                        if(cardData.getName().equalsIgnoreCase(name)){
                            card = cardData;
                            break;
                        }
                    }

                    if(card != null) {
                        if(sb){
                            deckData.getSideboard().put(card, Integer.parseInt(count));
                        }else {
                            deckData.getMainboard().put(card, Integer.parseInt(count));
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return deckData;
    }

    private static DeckData openDeck(File file, HashMap<String, CardData> allCards){
        DeckData deckData = new DeckData();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            Element[] elements = new Element[4];

            NodeList sections = doc.getElementsByTagName("section");
            for(int i=0; i<sections.getLength(); i++){
                Element element = (Element) sections.item(i);
                switch(element.getAttribute("id")){
                    case "commander":
                        elements[0] = element;
                        break;
                    case "main":
                        elements[1] = element;
                        break;
                    case "sideboard":
                        elements[2] = element;
                        break;
                    case "maybeboard":
                        elements[3] = element;
                        break;
                }
            }
            for(int i=0; i<elements.length; i++){
                if(elements[i] != null) {
                    NodeList children = elements[i].getChildNodes();
                    for (int j = 0; j < children.getLength(); j++) {
                        org.w3c.dom.Node child = children.item(j);
                        if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element element = (Element) child;
                            Element attr = ((Element) element.getElementsByTagName("card").item(0));

                            String name = element.getAttribute("id");
                            String setCode = attr.getAttribute("set");
                            int count = Integer.parseInt(attr.getAttribute("count"));

                            CardData card = null;
                            for (CardData cardData : allCards.values()) {
                                if (cardData.getName().equalsIgnoreCase(name) && cardData.getSetCode().equalsIgnoreCase(setCode)) {
                                    card = cardData;
                                    break;
                                }
                            }

                            if (card != null) {
                                switch(i){
                                    case 0:
                                        deckData.getCommander().put(card, count);
                                        break;
                                    case 1:
                                        deckData.getMainboard().put(card, count);
                                        break;
                                    case 2:
                                        deckData.getSideboard().put(card, count);
                                        break;
                                    case 3:
                                        deckData.getMaybeboard().put(card, count);
                                        break;
                                }
                            }
                        }
                    }
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        return deckData;
    }

    private static DeckData openJDeck(File file, HashMap<String, CardData> allCards){
        DeckData deckData = new DeckData();

        try {
            JSONParser parser = new JSONParser();
            JSONObject deckObj = (JSONObject) parser.parse(new FileReader(file));

            if (deckObj.containsKey("name")) {
                deckData.setDeckName(deckObj.get("name").toString());
            }

            for(int i=0; i<4; i++){
                if (deckObj.containsKey("section" + i)) {
                    JSONArray cardsArray = (JSONArray) deckObj.get("section" + i);
                    for(Object card : cardsArray){
                        JSONObject cardObj = (JSONObject) card;
                        int count = 1;

                        if (cardObj.containsKey("count")) {
                            count = Integer.parseInt(cardObj.get("count").toString());
                        }

                        if (cardObj.containsKey("multiverseId")) {
                            for (CardData cardData : allCards.values()) {
                                if (cardData.getMultiverseId().equals(cardObj.get("multiverseId").toString())) {
                                    switch(i){
                                        case 0:
                                            deckData.getCommander().put(cardData, count);
                                            break;
                                        case 1:
                                            deckData.getMainboard().put(cardData, count);
                                            break;
                                        case 2:
                                            deckData.getSideboard().put(cardData, count);
                                            break;
                                        case 3:
                                            deckData.getMaybeboard().put(cardData, count);
                                            break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException | ParseException e1) {
            e1.printStackTrace();
        }

        return deckData;
    }
}
