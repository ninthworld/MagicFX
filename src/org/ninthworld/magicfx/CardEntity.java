package org.ninthworld.magicfx;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class CardEntity {

    private CardData cardData;
    private boolean flipped, tapped;
    private int plusCounters, redCounters, greenCounters, blueCounters, loyaltyCounters;

    public CardEntity(CardData cardData){
        this.cardData = cardData;
        this.flipped = false;
        this.tapped = false;
        this.plusCounters = 0;
        this.redCounters = 0;
        this.greenCounters = 0;
        this.blueCounters = 0;
        this.loyaltyCounters = 0;
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
}
