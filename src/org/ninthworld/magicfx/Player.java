package org.ninthworld.magicfx;

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
    private int life, poison, damage;

    public Player(){
        this.deckData = new DeckData();

        this.commander = null;
        this.hand = new ArrayList<>();
        this.graveyard = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.exile = new ArrayList<>();
        this.battlefield = new ArrayList<>();

        this.name = "";
        this.life = 20;
        this.poison = this.damage = 0;
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
                if(commander == null){
                    commander = key;
                }
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
}
