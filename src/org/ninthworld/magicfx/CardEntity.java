package org.ninthworld.magicfx;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class CardEntity {

    private CardData cardData;
    private boolean flipped, tapped;
    private int counters; // Etc...

    public CardEntity(CardData cardData){
        this.cardData = cardData;
        this.flipped = false;
        this.tapped = false;
        this.counters = 0;
    }

    public CardData getCardData() {
        return cardData;
    }

    public void setCardData(CardData cardData) {
        this.cardData = cardData;
    }

    public int getCounters() {
        return counters;
    }

    public void setCounters(int counters) {
        this.counters = counters;
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
}
