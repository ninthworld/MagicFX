package org.ninthworld.magicfx;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class DeckData {

    private String deckName;
    private HashMap<CardData, Integer> commander, mainboard, sideboard, maybeboard;

    public DeckData(){
        this.deckName = "";
        this.commander = new HashMap<>();
        this.mainboard = new HashMap<>();
        this.sideboard = new HashMap<>();
        this.maybeboard = new HashMap<>();
    }

    public HashMap<CardData, Integer> getCommander() {
        return commander;
    }

    public void setCommander(HashMap<CardData, Integer> commander) {
        this.commander = commander;
    }

    public HashMap<CardData, Integer> getMainboard() {
        return mainboard;
    }

    public void setMainboard(HashMap<CardData, Integer> mainboard) {
        this.mainboard = mainboard;
    }

    public HashMap<CardData, Integer> getSideboard() {
        return sideboard;
    }

    public void setSideboard(HashMap<CardData, Integer> sideboard) {
        this.sideboard = sideboard;
    }

    public HashMap<CardData, Integer> getMaybeboard() {
        return maybeboard;
    }

    public void setMaybeboard(HashMap<CardData, Integer> maybeboard) {
        this.maybeboard = maybeboard;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }
}
