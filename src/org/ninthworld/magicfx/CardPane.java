package org.ninthworld.magicfx;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class CardPane extends BorderPane {

    public static final double cardHeightAnchor = 100;

    private boolean showCost, selected, dragged;
    private CardEntity cardEntity;
    private ResourceManager resourceManager;

    private Pane centerPane;
    private ImageView cardImageView;
    private TextFlow cardNameTextFlow, cardPTTextFlow;
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
        this.manaCostHBox = new HBox();
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

    private void applyCard(){

        if(!selected) {
            this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(2))));
        }else{
            this.setBorder(new Border(new BorderStroke(Color.rgb(77, 144, 254), BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(2))));
        }

        if(dragged){
            this.setEffect(new ColorAdjust(0, 0, -0.5, 0));
            this.setOpacity(0.4);
        }

        Image img = null;
        if (cardEntity.isFlipped()) {
            img = resourceManager.getCardBackImage();
        }else{
            img = resourceManager.getCardImage(cardEntity.getCardData());
        }

        cardImageView.setImage(img);
        cardImageView.setFitHeight(CardPane.cardHeightAnchor*(resourceManager.getScene().getHeight()/1080));
        cardImageView.setPreserveRatio(true);
        cardImageView.setSmooth(true);
        cardImageView.setCache(true);

        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.setRadius(1.6);
        cardImageView.setEffect(gaussianBlur);

        this.setMinWidth(cardImageView.getFitWidth());
        this.setMinHeight(cardImageView.getFitHeight());

        String finalName = "";
        String[] nameParts = cardEntity.getCardData().getName().split(" ");
        int runningCount = 0;
        for(String part : nameParts){
            if(runningCount + part.length() <= 10){
                runningCount += part.length();
            }else{
                runningCount = 0;
                if(finalName != ""){
                    finalName += "\n";
                }
            }

            finalName += part + " ";
        }

        Font font1 = new Font("Lucida Sans", CardPane.cardHeightAnchor*.12*(resourceManager.getScene().getHeight()/1080));
        Font font2 = new Font("Lucida Sans", CardPane.cardHeightAnchor*.14*(resourceManager.getScene().getHeight()/1080));

        DropShadow ds = new DropShadow();
        ds.setColor(Color.rgb(0, 0, 0, 0.7));
        ds.setRadius(1.75);
        ds.setSpread(1);

        cardNameTextFlow.setTextAlignment(TextAlignment.CENTER);
        cardNameTextFlow.setPrefWidth(CardPane.cardHeightAnchor*.7*(resourceManager.getScene().getHeight()/1080));
        Text cardName = new Text();
        cardName.setFont(font1);
        cardName.setFill(Color.WHITE);
        cardName.setEffect(ds);
        cardName.setText(finalName);
        cardNameTextFlow.getChildren().setAll(cardName);

        Text cardPT = new Text();
        if(!cardEntity.getCardData().getPower().equals("")) {
            cardPTTextFlow.setTextAlignment(TextAlignment.RIGHT);
            cardPTTextFlow.setPrefWidth(CardPane.cardHeightAnchor * .7 * (resourceManager.getScene().getHeight() / 1080));
            cardPTTextFlow.setTranslateY(CardPane.cardHeightAnchor * .8 * (resourceManager.getScene().getHeight() / 1080));
            cardPT.setFont(font2);
            cardPT.setFill(Color.WHITE);
            cardPT.setEffect(ds);
            cardPT.setText(cardEntity.getCardData().getPower() + "/" + cardEntity.getCardData().getToughness());
            cardPTTextFlow.getChildren().setAll(cardPT);
        }

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
                double cardWidth = CardPane.cardHeightAnchor*.7*(resourceManager.getScene().getHeight()/1080);
                double allIconWidth = manaIcons.size()*iconWidth;

                if(allIconWidth > cardWidth){
                    iconWidth -= (allIconWidth - cardWidth)/manaIcons.size();
                    allIconWidth = cardWidth;
                }

                manaCostHBox.setTranslateY(CardPane.cardHeightAnchor * .5 * (resourceManager.getScene().getHeight() / 1080));
                manaCostHBox.setTranslateX(cardWidth/2 - allIconWidth/2);
                for (ImageView icon : manaIcons) {
                    icon.setFitWidth(iconWidth);
                }
                manaCostHBox.getChildren().setAll(manaIcons);

                manaCostHBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, .7), new CornerRadii(8), new Insets(-2))));
            }
            manaCostHBox.setVisible(true);
        }else{
            manaCostHBox.setVisible(false);
        }

        resourceManager.getScene().heightProperty().addListener(e -> {
            double cardHeight = CardPane.cardHeightAnchor*(resourceManager.getScene().getHeight()/1080);
            double cardWidth = cardHeight*.7;

            cardImageView.setFitHeight(cardHeight);
            this.setMinHeight(cardImageView.getFitHeight());
            cardNameTextFlow.setPrefWidth(cardWidth);
            cardName.setFont(new Font("Lucida Sans", cardHeight*.12));

            cardPTTextFlow.setPrefWidth(cardWidth);
            cardPTTextFlow.setTranslateY(cardHeight*.8);
            cardPT.setFont(new Font("Lucida Sans", cardHeight*.14));

            if (manaIcons.size() > 0) {
                double iconWidth = manaIcons.get(0).getImage().getWidth();
                double allIconWidth = manaIcons.size()*iconWidth;

                if(allIconWidth > cardWidth){
                    iconWidth -= (allIconWidth - cardWidth)/manaIcons.size();
                    allIconWidth = cardWidth;
                }

                manaCostHBox.setTranslateY(cardHeight*.5);
                manaCostHBox.setTranslateX(cardWidth/2 - allIconWidth/2);
                for (ImageView icon : manaIcons) {
                    icon.setFitWidth(iconWidth);
                }
                manaCostHBox.getChildren().setAll(manaIcons);
            }
        });
    }

    public static CardPane createCardPane(CardEntity entity, ResourceManager resourceManager) {
        CardPane cardPane = new CardPane(entity, resourceManager);
        cardPane.setCenter(cardPane.centerPane);
        cardPane.centerPane.getChildren().setAll(cardPane.cardImageView, cardPane.cardNameTextFlow, cardPane.cardPTTextFlow, cardPane.manaCostHBox);
        cardPane.applyCard();
        return cardPane;
    }
}
