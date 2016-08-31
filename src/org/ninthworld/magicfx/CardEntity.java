package org.ninthworld.magicfx;

import org.json.simple.JSONObject;
import org.ninthworld.magicfx.client.ResourceManager;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class CardEntity {

    private CardData cardData;
    private boolean flipped, tapped;
    private int plusCounters, redCounters, greenCounters, blueCounters, loyaltyCounters;
    private double posX, posY;

    public CardEntity(CardData cardData){
        this.cardData = cardData;
        this.flipped = false;
        this.tapped = false;
        this.plusCounters = 0;
        this.redCounters = 0;
        this.greenCounters = 0;
        this.blueCounters = 0;
        this.loyaltyCounters = 0;
        this.posX = this.posY = 0;
        if(!cardData.getLoyalty().equals("")){
            this.loyaltyCounters = Integer.parseInt(cardData.getLoyalty());
        }
    }

    public CardData getCardData() {
        return cardData;
    }

    public void setCardData(CardData cardData) {
        this.cardData = cardData;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public boolean isTapped() {
        return tapped;
    }

    public void setTapped(boolean tapped) {
        this.tapped = tapped;
    }

    public int getPlusCounters() {
        return plusCounters;
    }

    public void setPlusCounters(int plusCounters) {
        this.plusCounters = plusCounters;
    }

    public int getRedCounters() {
        return redCounters;
    }

    public void setRedCounters(int redCounters) {
        this.redCounters = redCounters;
    }

    public int getGreenCounters() {
        return greenCounters;
    }

    public void setGreenCounters(int greenCounters) {
        this.greenCounters = greenCounters;
    }

    public int getBlueCounters() {
        return blueCounters;
    }

    public void setBlueCounters(int blueCounters) {
        this.blueCounters = blueCounters;
    }

    public int getLoyaltyCounters() {
        return loyaltyCounters;
    }

    public void setLoyaltyCounters(int loyaltyCounters) {
        this.loyaltyCounters = loyaltyCounters;
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("cardData", cardData.getMultiverseId());
        jsonObject.put("flipped", flipped);
        jsonObject.put("tapped", tapped);
        jsonObject.put("plusCounters", plusCounters);
        jsonObject.put("redCounters", redCounters);
        jsonObject.put("greenCounters", greenCounters);
        jsonObject.put("blueCounters", blueCounters);
        jsonObject.put("loyaltyCounters", loyaltyCounters);
        jsonObject.put("posX", posX);
        jsonObject.put("posY", posY);

        return jsonObject;
    }

    public static CardEntity getFromJSON(JSONObject jsonObject, ResourceManager resourceManager){
        CardData cardData = new CardData();
        cardData.setMultiverseId((String) jsonObject.get("cardData"));

        if(resourceManager != null){
            cardData = resourceManager.getAllCards().get((String) jsonObject.get("cardData"));
        }

        CardEntity cardEntity = new CardEntity(cardData);
        cardEntity.setFlipped((Boolean) jsonObject.get("flipped"));
        cardEntity.setTapped((Boolean) jsonObject.get("tapped"));
        cardEntity.setPlusCounters(((Long) jsonObject.get("plusCounters")).intValue());
        cardEntity.setRedCounters(((Long) jsonObject.get("redCounters")).intValue());
        cardEntity.setGreenCounters(((Long) jsonObject.get("greenCounters")).intValue());
        cardEntity.setBlueCounters(((Long) jsonObject.get("blueCounters")).intValue());
        cardEntity.setLoyaltyCounters(((Long) jsonObject.get("loyaltyCounters")).intValue());
        cardEntity.setPosX((Double) jsonObject.get("posX"));
        cardEntity.setPosY((Double) jsonObject.get("posY"));
        return cardEntity;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }
}
