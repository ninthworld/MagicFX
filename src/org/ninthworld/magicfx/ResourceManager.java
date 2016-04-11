package org.ninthworld.magicfx;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class ResourceManager {

    private static final String hqCardCachePath = "../DeckEditor/res/cache/hqcards/";
    private static final String lqCardCachePath = "res/cache/lqcards/";
    private static final String allSetsJSONPath = "/json/AllSets.json";
    private static final String cardBackPath = "/symbols/back.jpg";
    private static final String symbolsDir = "symbols";
    private static final String backgroundPath = "/bg/bg5.jpg";

    private Scene scene;
    private ImageView cardPreview;

    private ArrayList<CardData> allCards;
    private HashMap<CardData, Image> cardImages;
    private HashMap<String, Image> symbols;
    private HashMap<String, Color> rarityColors;

    private Image cardBackImage;
    private Image backgroundImage;
    private Color guiPaneBorderColor, guiPaneFillColor;

    public ResourceManager(Scene scene){
        this.scene = scene;
        this.cardPreview = new ImageView();

        this.allCards = new ArrayList<>();
        this.cardImages = new HashMap<>();
        this.symbols = new HashMap<>();
        this.rarityColors = new HashMap<>();
        this.backgroundImage = null;
        this.cardBackImage = null;
        this.guiPaneBorderColor = this.guiPaneFillColor = Color.BLACK;
    }

    public ArrayList<CardData> getAllCards(){
        return allCards;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void load(){
        try {
            setGuiPaneBorderColor(Color.rgb(0, 0, 0, 0.6));
            setGuiPaneFillColor(Color.rgb(255, 255, 255, 0.2));

            setCardBackImage(new Image(getClass().getResource(cardBackPath).toString()));
            setBackgroundImage(new Image(getClass().getResource(backgroundPath).toString()));

            loadAllSets();
            loadAllSymbols();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Image getCardImage(CardData card) {
        if(!cardImages.containsKey(card)) {
            File hqCard = new File(hqCardCachePath + card.getSetCode() + "/" + card.getNameUnmodified().replaceAll("\\\"", "") + (card.hasVariations() ? card.getVariationNum() : "") + ".full.jpg");
            if (!hqCard.exists()) {
                File lqCard = new File(lqCardCachePath + card.getSetCode() + "/" + card.getNameUnmodified().replaceAll("\\\"", "") + (card.hasVariations() ? card.getVariationNum() : "") + ".full.jpg");
                if (!lqCard.exists()) {
                    // Download
                    try {
                        URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + card.getMultiverseId() + "&type=card");
                        InputStream in = new BufferedInputStream(url.openStream());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        int n = 0;
                        while((n = in.read(buf)) != -1){
                            out.write(buf, 0, n);
                        }
                        out.close();
                        in.close();

                        new File(lqCard.getAbsolutePath().substring(0, lqCard.getAbsolutePath().lastIndexOf(File.separator))).mkdirs();
                        FileOutputStream fos = new FileOutputStream(lqCard);
                        fos.write(out.toByteArray());
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                cardImages.put(card, new Image(lqCard.toURI().toString()));
            } else {
                cardImages.put(card, new Image(hqCard.toURI().toString()));
            }
        }

        return cardImages.get(card);
    }

    private void loadAllSets() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject allSets = (JSONObject) parser.parse(new InputStreamReader(getClass().getResourceAsStream(allSetsJSONPath)));

        for(Object setCodeObj : allSets.keySet()){
            String setCode = setCodeObj.toString();
            JSONObject set = (JSONObject) allSets.get(setCodeObj);

            if(set.containsKey("cards")){
                JSONArray cardsArray = (JSONArray) set.get("cards");
                cardsArray.forEach(card->{
                    JSONObject cardObj = (JSONObject) card;
                    CardData cardData = new CardData();
                    cardData.setSetCode(setCode);
                    allCards.add(cardData);

                    if(cardObj.containsKey("name")){
                        cardData.setName(cardObj.get("name").toString());
                    }

                    if(cardObj.containsKey("manaCost")){
                        cardData.setManaCost(cardObj.get("manaCost").toString());
                    }

                    if(cardObj.containsKey("cmc")){
                        cardData.setCmc((int) Double.parseDouble(cardObj.get("cmc").toString()));
                    }

                    if(cardObj.containsKey("type")){
                        cardData.setType(cardObj.get("type").toString());
                    }

                    if(cardObj.containsKey("rarity")){
                        cardData.setRarity(cardObj.get("rarity").toString());
                    }

                    if(cardObj.containsKey("text")){
                        cardData.setText(cardObj.get("text").toString());
                    }

                    if(cardObj.containsKey("power")){
                        cardData.setPower(cardObj.get("power").toString());
                    }

                    if(cardObj.containsKey("toughness")){
                        cardData.setToughness(cardObj.get("toughness").toString());
                    }

                    if(cardObj.containsKey("names")){
                        JSONArray array = (JSONArray) cardObj.get("names");
                        String[] strs = new String[array.size()];
                        for(int i=0; i<strs.length; i++){
                            strs[i] = array.get(i).toString();
                        }
                        cardData.setNames(strs);
                    }

                    if(cardObj.containsKey("colors")){
                        JSONArray array = (JSONArray) cardObj.get("colors");
                        String[] strs = new String[array.size()];
                        for(int i=0; i<strs.length; i++){
                            strs[i] = array.get(i).toString();
                        }
                        cardData.setColors(strs);
                    }

                    if(cardObj.containsKey("colorIdentity")){
                        JSONArray array = (JSONArray) cardObj.get("colorIdentity");
                        String[] strs = new String[array.size()];
                        for(int i=0; i<strs.length; i++){
                            strs[i] = array.get(i).toString();
                        }
                        cardData.setColorIdentity(strs);
                    }

                    if(cardObj.containsKey("supertypes")){
                        JSONArray array = (JSONArray) cardObj.get("supertypes");
                        String[] strs = new String[array.size()];
                        for(int i=0; i<strs.length; i++){
                            strs[i] = array.get(i).toString();
                        }
                        cardData.setSupertypes(strs);
                    }

                    if(cardObj.containsKey("types")){
                        JSONArray array = (JSONArray) cardObj.get("types");
                        String[] strs = new String[array.size()];
                        for(int i=0; i<strs.length; i++){
                            strs[i] = array.get(i).toString();
                        }
                        cardData.setTypes(strs);
                    }

                    if(cardObj.containsKey("subtypes")){
                        JSONArray array = (JSONArray) cardObj.get("subtypes");
                        String[] strs = new String[array.size()];
                        for(int i=0; i<strs.length; i++){
                            strs[i] = array.get(i).toString();
                        }
                        cardData.setSubtypes(strs);
                    }

                    if(cardObj.containsKey("variations")){
                        JSONArray array = (JSONArray) cardObj.get("variations");
                        String[] strs = new String[array.size()];
                        for(int i=0; i<strs.length; i++){
                            strs[i] = array.get(i).toString();
                        }
                        cardData.setVariations(strs);
                    }

                    if(cardObj.containsKey("multiverseid")){
                        cardData.setMultiverseId(cardObj.get("multiverseid").toString());
                    }
                });
            }
        }
    }

    private void loadAllSymbols() throws IOException {
        File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        if(jarFile.isFile()) {
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries();
            while(entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith(symbolsDir + "/")) {
                    if (name.contains(".")) {
                        String clipped = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));
                        symbols.put(clipped, new Image(name));
                    }
                }
            }
            jar.close();
        } else {
            final URL url = getClass().getResource("/" + symbolsDir);
            if (url != null) {
                try {
                    final File apps = new File(url.toURI());
                    for (File app : apps.listFiles()) {
                        String name = app.toString();
                        String clipped = name.substring(name.lastIndexOf("\\") + 1, name.lastIndexOf("."));
                        symbols.put(clipped, new Image(new File(name).toURI().toString()));
                    }
                } catch (URISyntaxException ex) {
                }
            }
        }
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Color getGuiPaneBorderColor() {
        return guiPaneBorderColor;
    }

    public void setGuiPaneBorderColor(Color guiPaneBorderColor) {
        this.guiPaneBorderColor = guiPaneBorderColor;
    }

    public Color getGuiPaneFillColor() {
        return guiPaneFillColor;
    }

    public void setGuiPaneFillColor(Color guiPaneFillColor) {
        this.guiPaneFillColor = guiPaneFillColor;
    }

    public CardData getCardData(String cardName){
        for(CardData cardData : allCards){
            if(cardData.getName().equalsIgnoreCase(cardName)){
                return cardData;
            }
        }

        return new CardData();
    }

    public Image getCardBackImage() {
        return cardBackImage;
    }

    public void setCardBackImage(Image cardBackImage) {
        this.cardBackImage = cardBackImage;
    }

    public ImageView getCardPreview() {
        return cardPreview;
    }

    public void setCardPreview(ImageView cardPreview) {
        this.cardPreview = cardPreview;
    }
}
