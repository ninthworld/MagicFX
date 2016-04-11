package org.ninthworld.magicfx;

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

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class CardPane extends BorderPane {

    public static final double cardHeightAnchor = 100;

    private boolean showCost, selected, dragged;
    private CardEntity cardEntity;
    private ResourceManager resourceManager;
    private ImageView cardImageView;

    public CardPane(CardEntity cardEntity, ResourceManager resourceManager){
        super();
        this.selected = this.dragged = false;
        this.showCost = true;
        this.cardEntity = cardEntity;
        this.resourceManager = resourceManager;
        this.cardImageView = new ImageView();
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

        Pane pane = (Pane) this.getCenter();
        Font font = new Font("Lucida Sans", CardPane.cardHeightAnchor*.12*(resourceManager.getScene().getHeight()/1080));

        DropShadow ds = new DropShadow();
        ds.setColor(Color.rgb(0, 0, 0, 0.3));
        ds.setRadius(1.75);
        ds.setSpread(1);

        TextFlow cardNameFlow = new TextFlow();
        cardNameFlow.setTextAlignment(TextAlignment.CENTER);
        cardNameFlow.setPrefWidth(CardPane.cardHeightAnchor*.7*(resourceManager.getScene().getHeight()/1080));

        Text cardName = new Text();
        cardName.setFont(font);
        cardName.setFill(Color.WHITE);
        cardName.setEffect(ds);
        cardName.setText(finalName);

        TextFlow cardPTFlow = new TextFlow();
        Text cardPT = new Text();
        if(!showCost) {
            cardPTFlow.setTextAlignment(TextAlignment.RIGHT);
            cardPTFlow.setPrefWidth(CardPane.cardHeightAnchor * .7 * (resourceManager.getScene().getHeight() / 1080));
            cardPT.setFont(font);
            cardPT.setFill(Color.WHITE);
            cardPT.setEffect(ds);
            cardPT.setText(cardEntity.getCardData().getPower() + "/" + cardEntity.getCardData().getToughness());
        }

        cardNameFlow.getChildren().add(cardName);
        pane.getChildren().add(cardNameFlow);

        resourceManager.getScene().heightProperty().addListener(e -> {
            cardImageView.setFitHeight(CardPane.cardHeightAnchor*(resourceManager.getScene().getHeight()/1080));
            this.setMinHeight(cardImageView.getFitHeight());
            cardNameFlow.setPrefWidth(CardPane.cardHeightAnchor*.7*(resourceManager.getScene().getHeight()/1080));
            cardName.setFont(new Font("Lucida Sans", CardPane.cardHeightAnchor*.12*(resourceManager.getScene().getHeight()/1080)));
            cardPTFlow.setPrefWidth(CardPane.cardHeightAnchor*.7*(resourceManager.getScene().getHeight()/1080));
            cardPT.setFont(new Font("Lucida Sans", CardPane.cardHeightAnchor*.12*(resourceManager.getScene().getHeight()/1080)));
        });
    }

    public static CardPane createCardPane(CardEntity entity, ResourceManager resourceManager) {
        CardPane cardPane = new CardPane(entity, resourceManager);
        Pane pane = new Pane();
        cardPane.cardImageView = new ImageView();
        pane.getChildren().add(cardPane.cardImageView);
        cardPane.setCenter(pane);
        cardPane.applyCard();
        return cardPane;
    }
}
