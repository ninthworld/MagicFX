package org.ninthworld.magicfx.client;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import net.coobird.thumbnailator.Thumbnails;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.ninthworld.magicfx.CardData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
    private static final String hqCardCachePath = "res/cache/hqcards/"; //"../DeckEditor/res/cache/hqcards/"; //
    private static final String lqCardCachePath = "res/cache/lqcards/";
    private static final String allSetsJSONPath = "/json/AllSets.json";
    private static final String cardBackPath = "/symbols/back.jpg";
    private static final String symbolsDir = "symbols";
    private static final String backgroundPath = "/bg/bg5.jpg";
    public static final String lucidaFontPath = "/fonts/LucidaSansDemiBold.ttf";

    private Scene scene;
    private ImageView cardPreview;

    private HashMap<String, CardData> allCards;
    private HashMap<CardData, BufferedImage> cardImages;
    private HashMap<String, Image> symbols;
    private HashMap<String, Color> rarityColors;

    private BufferedImage cardBackImage;
    private Image backgroundImage;
    private Color guiPaneBorderColor, guiPaneFillColor;

    public ResourceManager(Scene scene){
        this.scene = scene;
        this.cardPreview = new ImageView();

        this.allCards = new HashMap<>();
        this.cardImages = new HashMap<>();
        this.symbols = new HashMap<>();
        this.rarityColors = new HashMap<>();
        this.backgroundImage = null;
        this.cardBackImage = null;
        this.guiPaneBorderColor = this.guiPaneFillColor = Color.BLACK;
    }

    public HashMap<String, Image> getSymbols(){
        return symbols;
    }

    public HashMap<String, CardData> getAllCards(){
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

            loadAllSets();
            loadAllSymbols();

            //setCardBackBufferedImage(ImageIO.read(new File(cardBackPath)));
            cardBackImage = ImageIO.read(getClass().getResourceAsStream(cardBackPath));
            setBackgroundImage(new Image(getClass().getResource(backgroundPath).toString()));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Image getCardImage(CardData card, double cardWidth, double cardHeight){
        Image cardImage = null;
        BufferedImage img = getCardBufferedImage(card);
        try {
            if(img != null) {
                BufferedImage bImg = Thumbnails.of(img).size((int) cardWidth, (int) cardHeight).asBufferedImage();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(bImg, "PNG", os);
                cardImage = new Image(new ByteArrayInputStream(os.toByteArray()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cardImage;
    }

    public BufferedImage getCardBufferedImage(CardData card) {
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

                        BufferedImage img = ImageIO.read(lqCard);
                        if(img != null) {
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            ImageIO.write(img.getSubimage(6, 6, 223 - 12, 310 - 12), "PNG", os);
                            os.writeTo(new FileOutputStream(lqCard));
                            os.flush();
                            os.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    cardImages.put(card, ImageIO.read(lqCard));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    cardImages.put(card, ImageIO.read(hqCard));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

                    if(cardObj.containsKey("multiverseid")){
                        cardData.setMultiverseId(cardObj.get("multiverseid").toString());
                    }

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

                    if(cardObj.containsKey("loyalty")){
                        cardData.setLoyalty(cardObj.get("loyalty").toString());
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

                    allCards.put(cardData.getMultiverseId(), cardData);
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
        for(CardData cardData : allCards.values()){
            if(cardData.getName().equalsIgnoreCase(cardName)){
                return cardData;
            }
        }

        return new CardData();
    }

    public Image getCardBackImage(double width, double height){
        Image cardImage = null;
        BufferedImage img = getCardBackBufferedImage();
        try {
            if(img != null) {
                BufferedImage bImg = Thumbnails.of(img).size((int) width, (int) height).asBufferedImage();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(bImg, "PNG", os);
                cardImage = new Image(new ByteArrayInputStream(os.toByteArray()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cardImage;
    }

    public BufferedImage getCardBackBufferedImage() {
        return cardBackImage;
    }

    public void setCardBackBufferedImage(BufferedImage cardBackImage) {
        this.cardBackImage = cardBackImage;
    }

    public ImageView getCardPreview() {
        return cardPreview;
    }

    public void setCardPreview(ImageView cardPreview) {
        this.cardPreview = cardPreview;
    }
}
