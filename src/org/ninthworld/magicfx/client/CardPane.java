package org.ninthworld.magicfx.client;

import javafx.geometry.Insets;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.ninthworld.magicfx.CardEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class CardPane extends BorderPane {

    public static final double cardHeightAnchor = 100;

    private boolean showCost, selected, dragged;
    private CardEntity cardEntity;
    private ResourceManager resourceManager;

    private Pane centerPane, symbolPane;
    private ImageView cardImageView, cardSymbolImageView;
    private TextFlow cardNameTextFlow, cardPTTextFlow, cardLoyaltyCounters, cardPlusCounters, cardRedCounters, cardBlueCounters, cardGreenCounters;
    private HBox manaCostHBox;

    public CardPane(CardEntity cardEntity, ResourceManager resourceManager){
        super();
        this.selected = this.dragged = false;
        this.showCost = true;
        this.cardEntity = cardEntity;
        this.resourceManager = resourceManager;

        this.centerPane = new Pane();
        this.cardImageView = new ImageView();
        this.cardNameTextFlow = new TextFlow();
        this.cardPTTextFlow = new TextFlow();
        this.cardPlusCounters = new TextFlow();
        this.cardGreenCounters = new TextFlow();
        this.cardRedCounters = new TextFlow();
        this.cardBlueCounters = new TextFlow();
        this.cardLoyaltyCounters = new TextFlow();
        this.manaCostHBox = new HBox();
        this.symbolPane = new Pane();
        this.cardSymbolImageView = new ImageView();
    }

    public boolean isShowCost() {
        return showCost;
    }

    public void setShowCost(boolean showCost) {
        this.showCost = showCost;
        applyCard();
    }

    public CardEntity getCardEntity() {
        return cardEntity;
    }

    public void setCardEntity(CardEntity cardEntity) {
        this.cardEntity = cardEntity;
    }

    public void setPlusCounters(int i){
        cardEntity.setPlusCounters(i);
        applyCard();
    }

    public void setRedCounters(int i){
        cardEntity.setRedCounters(i);
        applyCard();
    }

    public void setBlueCounters(int i){
        cardEntity.setBlueCounters(i);
        applyCard();
    }

    public void setGreenCounters(int i){
        cardEntity.setGreenCounters(i);
        applyCard();
    }

    public void setLoyaltyCounters(int loyalty){
        cardEntity.setLoyaltyCounters(loyalty);
        applyCard();
    }

    public void setFlipped(boolean flipped){
        cardEntity.setFlipped(flipped);
        applyCard();
    }

    public void setTapped(boolean tapped){
        cardEntity.setTapped(tapped);
        applyCard();
    }

    public void setSelected(boolean selected){
        this.selected = selected;
        applyCard();
    }

    public void setDragged(boolean dragged){
        this.dragged = dragged;
        applyCard();
    }

    public boolean isDragged(){
        return dragged;
    }

    private void applyCard(){
        double cardHeight = 2 * (Math.floor(CardPane.cardHeightAnchor*(resourceManager.getScene().getHeight()/1080))/2);
        double cardWidth = .72 * cardHeight;

        // Card Border Pane
        this.setWidth(cardWidth + 2);
        this.setHeight(cardHeight + 2);
        this.setMinSize(getWidth(), getHeight());
        this.setPrefSize(getWidth(), getHeight());
        this.setMaxSize(getWidth(), getHeight());

        // Card Image View
        Image img = null;
        if (cardEntity.isFlipped()) {
            img = resourceManager.getCardBackImage(cardWidth, cardHeight);
        }else{
            img = resourceManager.getCardImage(cardEntity.getCardData(), cardWidth, cardHeight);
        }

        cardImageView.setImage(img);
        cardImageView.setSmooth(true);
        cardImageView.setCache(true);
        cardImageView.setFitHeight(cardHeight);
        cardImageView.setFitWidth(cardWidth);

        // If Tapped
        if(cardEntity.isTapped()){
            this.setRotate(90);
        }else{
            this.setRotate(0);
        }

        // If Selected
        if(!selected) {
            this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(1))));
        }else{
            this.setBorder(new Border(new BorderStroke(Color.rgb(77, 144, 254), BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(1))));
        }

        // If Dragged
        if(dragged){
            this.setEffect(new ColorAdjust(0, 0, -0.5, 0));
            this.setOpacity(0.4);
        }

        String strokeStyle = "-fx-effect: dropshadow(one-pass-box, black, 4, 1, 0, 0);";
        String strokeStyle2 = "-fx-effect: dropshadow(one-pass-box, rgb(100, 80, 0), 4, 1, 0, 0);";

        InputStream is = getClass().getResourceAsStream(resourceManager.lucidaFontPath);
        Font font1 = Font.loadFont(is, .1 * cardHeight);
        Font font2 = Font.loadFont(is, .12 * cardHeight);
        Font font3 = Font.loadFont(is, .09 * cardHeight);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cardNameTextFlow.setTextAlignment(TextAlignment.CENTER);
        cardNameTextFlow.setMaxWidth(cardWidth);
        cardNameTextFlow.setTranslateY(.01 * cardHeight);
        Text cardName = new Text();
        cardName.setFont(font1);
        cardName.setStyle(strokeStyle);
        cardName.setFill(Color.WHITE);
        cardName.setText(cardEntity.getCardData().getName());
        cardNameTextFlow.getChildren().setAll(cardName);

        Text cardPT = new Text();
        if(!cardEntity.getCardData().getToughness().equals("")) {
            cardPTTextFlow.setTextAlignment(TextAlignment.RIGHT);
            cardPTTextFlow.setPrefWidth(cardWidth);
            cardPTTextFlow.setTranslateY(.86 * cardHeight);
            cardPT.setFont(font2);
            cardPT.setStyle(strokeStyle);
            cardPT.setFill(Color.WHITE);
            cardPT.setText(cardEntity.getCardData().getPower() + "/" + cardEntity.getCardData().getToughness());
            cardPTTextFlow.getChildren().setAll(cardPT);
        }

        Text cardLCT = new Text();
        if(!cardEntity.getCardData().getLoyalty().equals("")){
            cardLoyaltyCounters.setTextAlignment(TextAlignment.CENTER);
            cardLoyaltyCounters.setTranslateX(.78 * cardWidth);
            cardLoyaltyCounters.setTranslateY(cardHeight - .22 * cardWidth);
            cardLoyaltyCounters.setPrefWidth(.2 * cardWidth);
            cardLoyaltyCounters.setPrefHeight(.2 * cardWidth);
            cardLoyaltyCounters.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 255), new CornerRadii(8), new Insets(0))));
            cardLoyaltyCounters.setBorder(new Border(new BorderStroke(Color.rgb(153, 25, 152), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
            cardLCT.setFont(font3);
            cardLCT.setStyle(strokeStyle);
            cardLCT.setFill(Color.WHITE);
            cardLCT.setText(Integer.toString(cardEntity.getLoyaltyCounters()));
            cardLCT.setTranslateY(1);
        }
        cardLoyaltyCounters.getChildren().setAll(cardLCT);

        Text cardPCT = new Text();
        if(cardEntity.getPlusCounters() > 0){
            cardPlusCounters.setVisible(true);
            cardPlusCounters.setTextAlignment(TextAlignment.CENTER);
            cardPlusCounters.setTranslateX(.16 * cardWidth);
            cardPlusCounters.setTranslateY(.5 * cardHeight);
            cardPlusCounters.setPrefWidth(.68 * cardWidth);
            cardPlusCounters.setPrefHeight(.14 * cardHeight);
            cardPlusCounters.setBackground(new Background(new BackgroundFill(Color.rgb(200, 160, 0), new CornerRadii(8), new Insets(0))));
            cardPlusCounters.setBorder(new Border(new BorderStroke(Color.rgb(100, 80, 0), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
            cardPCT.setFont(font3);
            cardPCT.setStyle(strokeStyle);
            cardPCT.setFill(Color.WHITE);
            cardPCT.setText("+" + cardEntity.getPlusCounters() + "/+" + cardEntity.getPlusCounters());
        }else if(cardEntity.getPlusCounters() < 0){
            cardPlusCounters.setVisible(true);
            cardPlusCounters.setTextAlignment(TextAlignment.CENTER);
            cardPlusCounters.setTranslateX(.16 * cardWidth);
            cardPlusCounters.setTranslateY(.5 * cardHeight);
            cardPlusCounters.setPrefWidth(.68 * cardWidth);
            cardPlusCounters.setPrefHeight(.14 * cardHeight);
            cardPlusCounters.setBackground(new Background(new BackgroundFill(Color.rgb(80, 80, 80), new CornerRadii(8), new Insets(0))));
            cardPlusCounters.setBorder(new Border(new BorderStroke(Color.rgb(40, 40, 40), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
            cardPCT.setFont(font3);
            cardPCT.setStyle(strokeStyle);
            cardPCT.setFill(Color.WHITE);
            cardPCT.setText("-" + -1*cardEntity.getPlusCounters() + "/-" + -1*cardEntity.getPlusCounters());
        }else{
            cardPlusCounters.setVisible(false);
        }
        cardPlusCounters.getChildren().setAll(cardPCT);

        Text cardRCT = new Text();
        if(cardEntity.getRedCounters() > 0){
            cardRedCounters.setVisible(true);
            cardRedCounters.setTextAlignment(TextAlignment.CENTER);
            cardRedCounters.setTranslateX(.2 * cardWidth);
            cardRedCounters.setTranslateY(.5 * cardHeight - .2 * cardWidth);
            cardRedCounters.setPrefWidth(.2 * cardWidth);
            cardRedCounters.setPrefHeight(.2 * cardWidth);
            cardRedCounters.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0), new CornerRadii(8), new Insets(0))));
            cardRedCounters.setBorder(new Border(new BorderStroke(Color.rgb(160, 25, 0), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
            cardRCT.setFont(font3);
            cardRCT.setStyle(strokeStyle);
            cardRCT.setFill(Color.WHITE);
            cardRCT.setText(Integer.toString(cardEntity.getRedCounters()));
            cardRCT.setTranslateY(1);
        }else{
            cardRedCounters.setVisible(false);
        }
        cardRedCounters.getChildren().setAll(cardRCT);

        Text cardGCT = new Text();
        if(cardEntity.getGreenCounters() > 0){
            cardGreenCounters.setVisible(true);
            cardGreenCounters.setTextAlignment(TextAlignment.CENTER);
            cardGreenCounters.setTranslateX(.4 * cardWidth);
            cardGreenCounters.setTranslateY(.5 * cardHeight - .2 * cardWidth);
            cardGreenCounters.setPrefWidth(.2 * cardWidth);
            cardGreenCounters.setPrefHeight(.2 * cardWidth);
            cardGreenCounters.setBackground(new Background(new BackgroundFill(Color.rgb(0, 220, 0), new CornerRadii(8), new Insets(0))));
            cardGreenCounters.setBorder(new Border(new BorderStroke(Color.rgb(29, 143, 2), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
            cardGCT.setFont(font3);
            cardGCT.setStyle(strokeStyle);
            cardGCT.setFill(Color.WHITE);
            cardGCT.setText(Integer.toString(cardEntity.getGreenCounters()));
            cardGCT.setTranslateY(1);
        }else{
            cardGreenCounters.setVisible(false);
        }
        cardGreenCounters.getChildren().setAll(cardGCT);

        Text cardBCT = new Text();
        if(cardEntity.getBlueCounters() > 0){
            cardBlueCounters.setVisible(true);
            cardBlueCounters.setTextAlignment(TextAlignment.CENTER);
            cardBlueCounters.setTranslateX(.6 * cardWidth);
            cardBlueCounters.setTranslateY(.5 * cardHeight - .2 * cardWidth);
            cardBlueCounters.setPrefWidth(.2 * cardWidth);
            cardBlueCounters.setPrefHeight(.2 * cardWidth);
            cardBlueCounters.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255), new CornerRadii(8), new Insets(0))));
            cardBlueCounters.setBorder(new Border(new BorderStroke(Color.rgb(21, 14, 131), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1))));
            cardBCT.setFont(font3);
            cardBCT.setStyle(strokeStyle);
            cardBCT.setFill(Color.WHITE);
            cardBCT.setText(Integer.toString(cardEntity.getBlueCounters()));
            cardBCT.setTranslateY(1);
        }else{
            cardBlueCounters.setVisible(false);
        }
        cardBlueCounters.getChildren().setAll(cardBCT);

        ArrayList<ImageView> manaIcons = new ArrayList<>();
        if(showCost) {
            for (String symbol : cardEntity.getCardData().getManaSymbols()) {
                ImageView manaImgView = new ImageView();
                Image manaImg = null;
                if (resourceManager.getSymbols().containsKey(symbol)) {
                    manaImg = resourceManager.getSymbols().get(symbol);
                } else {
                    manaImg = resourceManager.getSymbols().get("CHAOS");
                }
                manaImgView.setImage(manaImg);
                manaImgView.setPreserveRatio(true);
                manaIcons.add(manaImgView);
            }

            if (manaIcons.size() > 0) {
                double iconWidth = manaIcons.get(0).getImage().getWidth();
                double allIconWidth = manaIcons.size()*iconWidth;

                if(allIconWidth > cardWidth){
                    iconWidth -= (allIconWidth - cardWidth)/manaIcons.size();
                    allIconWidth = cardWidth;
                }

                manaCostHBox.setTranslateY(.68 * cardHeight);
                manaCostHBox.setTranslateX(cardWidth/2 - allIconWidth/2);
                for (ImageView icon : manaIcons) {
                    icon.setFitWidth(iconWidth);
                }
                manaCostHBox.getChildren().setAll(manaIcons);

                manaCostHBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, .5), new CornerRadii(8), new Insets(-2))));
            }
            manaCostHBox.setVisible(true);
        }else{
            manaCostHBox.setVisible(false);
        }

        if(cardEntity.getCardData().getTypes() != null && cardEntity.getCardData().getTypes().length > 0){
            String type = cardEntity.getCardData().getTypes()[0];
            cardSymbolImageView.setImage(resourceManager.getSymbols().get(type));
            cardSymbolImageView.setFitWidth(.2 * cardWidth);
            cardSymbolImageView.setTranslateX(.025 * cardWidth);
            symbolPane.setPrefHeight(.2 * cardWidth);
            symbolPane.setPrefHeight(.2 * cardWidth);
            symbolPane.setTranslateY(.8 * cardHeight);
            symbolPane.setTranslateX(.05 * cardWidth);
            symbolPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, .4), new CornerRadii(2), new Insets(0))));
            symbolPane.setBorder(new Border(new BorderStroke(Color.rgb(80, 80, 80, .4), BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(1))));
        }
    }

    public static CardPane createCardPane(CardEntity entity, ResourceManager resourceManager) {
        CardPane cardPane = new CardPane(entity, resourceManager);
        cardPane.setCenter(cardPane.centerPane);
        cardPane.symbolPane.getChildren().add(cardPane.cardSymbolImageView);
        cardPane.centerPane.getChildren().setAll(
                cardPane.cardImageView,
                cardPane.cardNameTextFlow,
                cardPane.cardPTTextFlow,
                cardPane.cardPlusCounters,
                cardPane.cardGreenCounters,
                cardPane.cardBlueCounters,
                cardPane.cardRedCounters,
                cardPane.cardLoyaltyCounters,
                cardPane.manaCostHBox,
                cardPane.symbolPane
        );
        cardPane.applyCard();
        resourceManager.getScene().heightProperty().addListener(e -> {
            cardPane.applyCard();
        });
        return cardPane;
    }
}
