package org.ninthworld.magicfx;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ninthworld.magicfx.client.ResourceManager;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class Player {

    private DeckData deckData;

    private CardData commander;
    private ArrayList<CardData> hand, graveyard, deck;
    private ArrayList<CardEntity> battlefield, exile;

    private String name;
    private int life, poison, damage, commanderDamage, team;

    public Player(){
        this.deckData = null; //new DeckData();

        this.commander = null;
        this.hand = new ArrayList<>();
        this.graveyard = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.exile = new ArrayList<>();
        this.battlefield = new ArrayList<>();

        this.team = 1;
        this.name = "";
        this.life = 20;
        this.poison = this.commanderDamage = this.damage = 0;
    }

    public void initGame(){
        commander = null;
        hand = new ArrayList<>();
        graveyard = new ArrayList<>();
        deck = new ArrayList<>();
        exile = new ArrayList<>();
        battlefield = new ArrayList<>();

        if(deckData.getCommander().size() > 0){
            deckData.getCommander().keySet().forEach(key -> {
                commander = key;
            });
        }

        deckData.getMainboard().entrySet().forEach(e -> {
            for(int i=0; i<e.getValue(); i++) {
                deck.add(e.getKey());
            }
        });
        shuffleDeck();
    }

    public void shuffleDeck(){
        Collections.shuffle(deck);
    }

    public void drawCardFromDeckToHand(){
        if(deck.size() > 0){
            CardData topCard = deck.get(0);
            deck.remove(0);
            addCardToHand(topCard);
        }
    }

    public void addCardToHand(CardData card){
        hand.add(card);
    }

    public CardData getCommander() {
        return commander;
    }

    public void setCommander(CardData commander) {
        this.commander = commander;
    }

    public ArrayList<CardData> getHand() {
        return hand;
    }

    public void setHand(ArrayList<CardData> hand) {
        this.hand = hand;
    }

    public ArrayList<CardData> getGraveyard() {
        return graveyard;
    }

    public void setGraveyard(ArrayList<CardData> graveyard) {
        this.graveyard = graveyard;
    }

    public ArrayList<CardData> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<CardData> deck) {
        this.deck = deck;
    }

    public ArrayList<CardEntity> getExile() {
        return exile;
    }

    public void setExile(ArrayList<CardEntity> exile) {
        this.exile = exile;
    }

    public ArrayList<CardEntity> getBattlefield() {
        return battlefield;
    }

    public void setBattlefield(ArrayList<CardEntity> battlefield) {
        this.battlefield = battlefield;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getPoison() {
        return poison;
    }

    public void setPoison(int poison) {
        this.poison = poison;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public DeckData getDeckData() {
        return deckData;
    }

    public void setDeckData(DeckData deckData) {
        this.deckData = deckData;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getCommanderDamage() {
        return commanderDamage;
    }

    public void setCommanderDamage(int commanderDamage) {
        this.commanderDamage = commanderDamage;
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("name", name);
        jsonObject.put("life", life);
        jsonObject.put("poison", poison);
        jsonObject.put("damage", damage);
        jsonObject.put("commanderDamage", commanderDamage);
        jsonObject.put("team", team);

        // EASY PATH - VERY INEFFICENT
        JSONArray handArray = new JSONArray();
        JSONArray graveyardArray = new JSONArray();
        JSONArray deckArray = new JSONArray();
        JSONArray battlefieldArray = new JSONArray();
        JSONArray exileArray = new JSONArray();

        for(CardData cardData : hand){
            handArray.add(cardData.getMultiverseId());
        }

        for(CardData cardData : graveyard){
            graveyardArray.add(cardData.getMultiverseId());
        }

        for(CardData cardData : deck){
            deckArray.add(cardData.getMultiverseId());
        }

        for(CardEntity cardEntity : battlefield){
            battlefieldArray.add(cardEntity.toJSONObject());
        }

        for(CardEntity cardEntity : exile){
            exileArray.add(cardEntity.toJSONObject());
        }

        jsonObject.put("commander", (commander != null ? commander.getMultiverseId() : ""));
        jsonObject.put("hand", handArray);
        jsonObject.put("graveyard", graveyardArray);
        jsonObject.put("deck", deckArray);
        jsonObject.put("battlefield", battlefieldArray);
        jsonObject.put("exile", exileArray);
        jsonObject.put("isDeckLoaded", deckData != null);

        return jsonObject;
    }

    public void setFromJSON(JSONObject jsonObject, ResourceManager resourceManager){
        if(((Boolean) jsonObject.get("isDeckLoaded"))){
            deckData = new DeckData();
        }
        name = (String) jsonObject.get("name");
        life = ((Long) jsonObject.get("life")).intValue();
        poison = ((Long) jsonObject.get("poison")).intValue();
        damage = ((Long) jsonObject.get("damage")).intValue();
        commanderDamage = ((Long) jsonObject.get("commanderDamage")).intValue();
        team = ((Long) jsonObject.get("team")).intValue();

        // Inefficent
        if(resourceManager != null) {
            commander = resourceManager.getAllCards().get((String) jsonObject.get("commander"));

            hand.clear();
            JSONArray handArray = (JSONArray) jsonObject.get("hand");
            for (Object obj : handArray) {
                hand.add(resourceManager.getAllCards().get((String) obj));
            }

            graveyard.clear();
            JSONArray graveyardArray = (JSONArray) jsonObject.get("graveyard");
            for (Object obj : graveyardArray) {
                graveyard.add(resourceManager.getAllCards().get((String) obj));
            }

            deck.clear();
            JSONArray deckArray = (JSONArray) jsonObject.get("deck");
            for (Object obj : deckArray) {
                deck.add(resourceManager.getAllCards().get((String) obj));
            }
        }else{
            commander = new CardData();
            commander.setMultiverseId((String) jsonObject.get("commander"));

            hand.clear();
            JSONArray handArray = (JSONArray) jsonObject.get("hand");
            for (Object obj : handArray) {
                CardData cardData = new CardData();
                cardData.setMultiverseId((String) obj);
                hand.add(cardData);
            }

            graveyard.clear();
            JSONArray graveyardArray = (JSONArray) jsonObject.get("graveyard");
            for (Object obj : graveyardArray) {
                CardData cardData = new CardData();
                cardData.setMultiverseId((String) obj);
                graveyard.add(cardData);
            }

            deck.clear();
            JSONArray deckArray = (JSONArray) jsonObject.get("deck");
            for (Object obj : deckArray) {
                CardData cardData = new CardData();
                cardData.setMultiverseId((String) obj);
                deck.add(cardData);
            }
        }

        battlefield.clear();
        JSONArray battlefieldArray = (JSONArray) jsonObject.get("battlefield");
        for (Object obj : battlefieldArray) {
            battlefield.add(CardEntity.getFromJSON((JSONObject) obj, resourceManager));
        }

        exile.clear();
        JSONArray exileArray = (JSONArray) jsonObject.get("exile");
        for (Object obj : exileArray) {
            exile.add(CardEntity.getFromJSON((JSONObject) obj, resourceManager));
        }
    }
}
