package org.ninthworld.magicfx;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import javax.smartcardio.Card;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class PlayerArea {

    private GridPane parentGridPane, deckGravePane;
    private Pane battlefieldPane, exilePane, handPane, playerPane, commanderPane, graveyardPane, deckPane, manaPane;
    private TextFlow deckTextFlow, graveyardTextFlow, battlefieldTextFlow, exileTextFlow, handTextFlow, commanderTextFlow;

    private ArrayList<Pane> areaPanes;
    private ArrayList<CardPane> selectedNodes;
    private ArrayList<Drag> draggedNodes;

    private Player player;

    public PlayerArea(Player player){
        this.player = player;

        this.parentGridPane = new GridPane();
        this.battlefieldPane = new Pane();
        this.exilePane = new Pane();
        this.handPane = new Pane();
        this.deckGravePane = new GridPane();
        this.playerPane = new Pane();
        this.commanderPane = new Pane();
        this.graveyardPane = new Pane();
        this.deckPane = new Pane();
        this.manaPane = new Pane();

        this.deckTextFlow = new TextFlow();
        this.graveyardTextFlow = new TextFlow();
        this.battlefieldTextFlow = new TextFlow();
        this.exileTextFlow = new TextFlow();
        this.handTextFlow = new TextFlow();
        this.commanderTextFlow = new TextFlow();

        this.areaPanes = new ArrayList<>();
        this.selectedNodes = new ArrayList<>();
        this.draggedNodes = new ArrayList<>();
    }

    //<editor-fold desc="Getters and Setters">
    public TextFlow getBattlefieldTextFlow() {
        return battlefieldTextFlow;
    }

    public void setBattlefieldTextFlow(TextFlow battlefieldTextFlow) {
        this.battlefieldTextFlow = battlefieldTextFlow;
    }

    public TextFlow getExileTextFlow() {
        return exileTextFlow;
    }

    public void setExileTextFlow(TextFlow exileTextFlow) {
        this.exileTextFlow = exileTextFlow;
    }

    public TextFlow getHandTextFlow() {
        return handTextFlow;
    }

    public void setHandTextFlow(TextFlow handTextFlow) {
        this.handTextFlow = handTextFlow;
    }

    public TextFlow getCommanderTextFlow() {
        return commanderTextFlow;
    }

    public void setCommanderTextFlow(TextFlow commanderTextFlow) {
        this.commanderTextFlow = commanderTextFlow;
    }

    public TextFlow getDeckTextFlow() {
        return deckTextFlow;
    }

    public void setDeckTextFlow(TextFlow deckTextFlow) {
        this.deckTextFlow = deckTextFlow;
    }

    public TextFlow getGraveyardTextFlow() {
        return graveyardTextFlow;
    }

    public void setGraveyardTextFlow(TextFlow graveyardTextFlow) {
        this.graveyardTextFlow = graveyardTextFlow;
    }

    public GridPane getParentGridPane() {
        return parentGridPane;
    }

    public void setParentGridPane(GridPane parentGridPane) {
        this.parentGridPane = parentGridPane;
    }

    public Pane getBattlefieldPane() {
        return battlefieldPane;
    }

    public void setBattlefieldPane(Pane battlefieldPane) {
        this.battlefieldPane = battlefieldPane;
    }

    public Pane getExilePane() {
        return exilePane;
    }

    public void setExilePane(Pane exilePane) {
        this.exilePane = exilePane;
    }

    public Pane getHandPane() {
        return handPane;
    }

    public void setHandPane(Pane handPane) {
        this.handPane = handPane;
    }

    public GridPane getDeckGravePane() {
        return deckGravePane;
    }

    public void setDeckGravePane(GridPane deckGravePane) {
        this.deckGravePane = deckGravePane;
    }

    public Pane getPlayerPane() {
        return playerPane;
    }

    public void setPlayerPane(Pane playerPane) {
        this.playerPane = playerPane;
    }

    public Pane getCommanderPane() {
        return commanderPane;
    }

    public void setCommanderPane(Pane commanderPane) {
        this.commanderPane = commanderPane;
    }

    public ArrayList<Pane> getAreaPanes() {
        return areaPanes;
    }

    public void setAreaPanes(ArrayList<Pane> areaPanes) {
        this.areaPanes = areaPanes;
    }

    public ArrayList<CardPane> getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(ArrayList<CardPane> selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public ArrayList<Drag> getDraggedNodes() {
        return draggedNodes;
    }

    public void setDraggedNodes(ArrayList<Drag> draggedNodes) {
        this.draggedNodes = draggedNodes;
    }

    public Pane getGraveyardPane() {
        return graveyardPane;
    }

    public void setGraveyardPane(Pane graveyardPane) {
        this.graveyardPane = graveyardPane;
    }

    public Pane getDeckPane() {
        return deckPane;
    }

    public void setDeckPane(Pane deckPane) {
        this.deckPane = deckPane;
    }

    public Pane getManaPane() {
        return manaPane;
    }

    public void setManaPane(Pane manaPane) {
        this.manaPane = manaPane;
    }
    //</editor-fold>

    public void initAreas(ResourceManager resourceManager){
        CardEntity deckCard = new CardEntity(new CardData());
        CardPane cardPane = CardPane.createCardPane(deckCard, resourceManager);
        cardPane.setFlipped(true);
        deckPane.getChildren().add(cardPane);

        if(player.getCommander() != null){
            commanderPane.getChildren().add(CardPane.createCardPane(new CardEntity(player.getCommander()), resourceManager));
        }

        updatePanes(resourceManager);
        updatePaneActions(resourceManager);
    }

    public void updatePanes(ResourceManager resourceManager){
        updateBattlefieldPane(resourceManager);
        updateHandPane(resourceManager);
        updateExilePane(resourceManager);
        updateDeckPane(resourceManager);
        updateGraveyardPane(resourceManager);
        updateCommanderPane(resourceManager);

        /*System.out.printf("" +
                "Battlefield - Pane: %d, Array: %d\n" +
                "       Hand - Pane: %d, Array: %d\n" +
                "      Exile - Pane: %d, Array: %d\n" +
                "       Deck - Pane: %d, Array: %d\n" +
                "  Graveyard - Pane: %d, Array: %d\n" +
                "  Commander - Pane: %d, Array: %b\n" +
                "      Total = %d\n\n",
                battlefieldPane.getChildren().size(), player.getBattlefield().size(),
                handPane.getChildren().size(), player.getHand().size(),
                exilePane.getChildren().size(), player.getExile().size(),
                deckPane.getChildren().size(), player.getDeck().size(),
                graveyardPane.getChildren().size(), player.getGraveyard().size(),
                commanderPane.getChildren().size(), player.getCommander() != null,
                player.getBattlefield().size()+player.getHand().size()+player.getExile().size()+player.getDeck().size()+player.getGraveyard().size()
        );*/
    }

    public void moveCardFromTo(ResourceManager resourceManager, Pane fromPane, Pane toPane, CardPane fromCard, CardPane toCard){
        removeSelectedChild(selectedNodes, fromCard);
        CardEntity newCardEntity = fromCard.getCardEntity();

        if(fromPane == graveyardPane){
            player.getGraveyard().remove(fromCard.getCardEntity().getCardData());
            if(player.getGraveyard().size() > 0){
                CardEntity deckCard = new CardEntity(player.getGraveyard().get(player.getGraveyard().size()-1));
                graveyardPane.getChildren().add(CardPane.createCardPane(deckCard, resourceManager));
            }
        }

        if(fromPane == deckPane){
            if(player.getDeck().size() > 0) {
                newCardEntity.setCardData(player.getDeck().remove(player.getDeck().size()-1));
            }

            if(player.getDeck().size() > 0){
                CardEntity deckCard = new CardEntity(new CardData());
                CardPane cardPane = CardPane.createCardPane(deckCard, resourceManager);
                cardPane.setFlipped(true);
                deckPane.getChildren().add(cardPane);
            }
        }

        if(fromPane == battlefieldPane){
            player.getBattlefield().remove(fromCard.getCardEntity());
        }

        if(fromPane == exilePane){
            player.getExile().remove(fromCard.getCardEntity().getCardData());
        }

        if(fromPane == handPane){
            player.getHand().remove(fromCard.getCardEntity().getCardData());
        }

        if(fromPane == commanderPane){

        }

        if(toPane == graveyardPane){
            player.getGraveyard().add(newCardEntity.getCardData());

            newCardEntity = new CardEntity(newCardEntity.getCardData());
            CardPane newCardPane = CardPane.createCardPane(newCardEntity, resourceManager);

            ArrayList<Node> tempChildren = new ArrayList<>();
            graveyardPane.getChildren().forEach(tempChildren::add);
            for(Node node : tempChildren){
                if(node instanceof CardPane) {
                    graveyardPane.getChildren().remove(node);
                }
            }
            graveyardPane.getChildren().add(newCardPane);
        }

        if(toPane == deckPane){
            player.getDeck().add(newCardEntity.getCardData());
        }

        if(toPane == battlefieldPane){
            player.getBattlefield().add(newCardEntity);

            CardPane newCardPane = CardPane.createCardPane(newCardEntity, resourceManager);
            newCardPane.setTranslateX(toCard.getTranslateX());
            newCardPane.setTranslateY(toCard.getTranslateY());
            newCardPane.setShowCost(false);

            addSelectedChild(selectedNodes, newCardPane);

            battlefieldPane.getChildren().add(newCardPane);
        }

        if(toPane == exilePane){
            player.getExile().add(newCardEntity.getCardData());

            CardPane newCardPane = CardPane.createCardPane(newCardEntity, resourceManager);
            newCardPane.setTranslateX(toCard.getTranslateX());
            newCardPane.setTranslateY(toCard.getTranslateY());

            addSelectedChild(selectedNodes, newCardPane);

            exilePane.getChildren().add(newCardPane);
        }

        if(toPane == handPane){
            player.getHand().add(newCardEntity.getCardData());

            newCardEntity = new CardEntity(newCardEntity.getCardData());
            CardPane newCardPane = CardPane.createCardPane(newCardEntity, resourceManager);

            addSelectedChild(selectedNodes, newCardPane);

            handPane.getChildren().add(newCardPane);
        }

        if(toPane == commanderPane){
            if(fromCard.getCardEntity().getCardData() == player.getCommander()){
                commanderPane.getChildren().add(CardPane.createCardPane(newCardEntity, resourceManager));
            }else{
                CardPane cardPane = CardPane.createCardPane(newCardEntity, resourceManager);
                cardPane.setTranslateX(toCard.getTranslateX());
                cardPane.setTranslateY(toCard.getTranslateY());

                fromPane.getChildren().add(cardPane);
            }
        }

        updatePaneActions(resourceManager);
    }

    private void updateBattlefieldPane(ResourceManager resourceManager){
        double parentWidth = (battlefieldPane.getWidth() > 0 ? battlefieldPane.getWidth() : battlefieldPane.getPrefWidth());
        double parentHeight = (battlefieldPane.getHeight() > 0 ? battlefieldPane.getHeight() : battlefieldPane.getPrefHeight());
        ArrayList<Node> bNodes = new ArrayList<>();
        battlefieldPane.getChildren().forEach(child -> {
            if(child instanceof CardPane){
                bNodes.add(child);
            }
        });
        Collections.sort(bNodes, (n1, n2) -> {
            if(n1.getTranslateY() < n2.getTranslateY() || n1.getTranslateX() < n2.getTranslateX()){
                return -1;
            }
            return 0;
        });
        battlefieldPane.getChildren().setAll(bNodes);
    }

    private void updateHandPane(ResourceManager resourceManager){
        double parentWidth = (handPane.getWidth() > 0 ? handPane.getWidth() : handPane.getPrefWidth());
        double parentHeight = (handPane.getHeight() > 0 ? handPane.getHeight() : handPane.getPrefHeight());
        ArrayList<Node> hNodes = new ArrayList<>();
        handPane.getChildren().forEach(child -> {
            if(child instanceof CardPane){
                hNodes.add(child);
            }
        });
        Collections.sort(hNodes, (n1, n2) -> {
            if(n1.getTranslateX() < n2.getTranslateX()){
                return -1;
            }
            return 0;
        });

        hNodes.forEach(node -> {
            Bounds nodeBounds = node.getLayoutBounds();
            double padding = 8;
            if(hNodes.size()*(nodeBounds.getWidth()+padding) > parentWidth){
                padding -= (hNodes.size()*(nodeBounds.getWidth()+padding) - parentWidth + nodeBounds.getWidth()/2)/hNodes.size();
            }
            node.setTranslateX(parentWidth/2 - ((nodeBounds.getWidth() + padding)*hNodes.size())/2 + (nodeBounds.getWidth() + padding)*hNodes.indexOf(node));
            node.setTranslateY(parentHeight/2 - nodeBounds.getHeight()/2);
        });
    }

    private void updateExilePane(ResourceManager resourceManager){
        double parentWidth = (exilePane.getWidth() > 0 ? exilePane.getWidth() : exilePane.getPrefWidth());
        double parentHeight = (exilePane.getHeight() > 0 ? exilePane.getHeight() : exilePane.getPrefHeight());
        ArrayList<Node> eNodes = new ArrayList<>();
        exilePane.getChildren().forEach(child -> {
            if(child instanceof CardPane){
                eNodes.add(child);
            }
        });
        Collections.sort(eNodes, (n1, n2) -> {
            if(n1.getTranslateY() < n2.getTranslateY()){
                return -1;
            }
            return 0;
        });
        exilePane.getChildren().setAll(eNodes);

        eNodes.forEach(node -> {
            Bounds nodeBounds = node.getLayoutBounds();
            node.setTranslateX(parentWidth/2 - nodeBounds.getWidth()/2);
        });
    }

    private void updateGraveyardPane(ResourceManager resourceManager){
        double parentWidth = (graveyardPane.getWidth() > 0 ? graveyardPane.getWidth() : graveyardPane.getPrefWidth());
        double parentHeight = (graveyardPane.getHeight() > 0 ? graveyardPane.getHeight() : graveyardPane.getPrefHeight());

        Font font1 = Font.loadFont(resourceManager.lucidaFontURI, .1 * CardPane.cardHeightAnchor*(resourceManager.getScene().getHeight()/1080));
        String strokeStyle = "-fx-effect: dropshadow(one-pass-box, rgba(0, 0, 0, 0.6), 4, 1, 0, 0);";

        graveyardTextFlow.setTextAlignment(TextAlignment.CENTER);
        graveyardTextFlow.setPrefWidth(parentWidth);
        graveyardTextFlow.setTranslateY(parentHeight*.08);
        Text text = new Text();
        text.setFont(font1);
        text.setStyle(strokeStyle);
        text.setFill(Color.rgb(255, 255, 255, 0.6));
        text.setText("Graveyard (" + player.getGraveyard().size() + ")");
        graveyardTextFlow.getChildren().setAll(text);

        ArrayList<Node> gNodes = new ArrayList<>();
        graveyardPane.getChildren().forEach(child -> {
            gNodes.add(child);
            if(child instanceof CardPane){
                Bounds nodeBounds = child.getLayoutBounds();
                child.setTranslateX(parentWidth / 2 - nodeBounds.getWidth() / 2);
                child.setTranslateY(parentHeight / 2 - nodeBounds.getHeight() / 2);
            }
        });
        graveyardPane.getChildren().setAll(gNodes);
    }

    private void updateDeckPane(ResourceManager resourceManager){
        double parentWidth = (deckPane.getWidth() > 0 ? deckPane.getWidth() : deckPane.getPrefWidth());
        double parentHeight = (deckPane.getHeight() > 0 ? deckPane.getHeight() : deckPane.getPrefHeight());

        Font font1 = Font.loadFont(resourceManager.lucidaFontURI, .1 * CardPane.cardHeightAnchor*(resourceManager.getScene().getHeight()/1080));
        String strokeStyle = "-fx-effect: dropshadow(one-pass-box, rgba(0, 0, 0, 0.6), 4, 1, 0, 0);";

        deckTextFlow.setTextAlignment(TextAlignment.CENTER);
        deckTextFlow.setPrefWidth(parentWidth);
        deckTextFlow.setTranslateY(parentHeight*.08);
        Text text = new Text();
        text.setFont(font1);
        text.setStyle(strokeStyle);
        text.setFill(Color.rgb(255, 255, 255, 0.6));
        text.setText("Library (" + player.getDeck().size() + ")");
        deckTextFlow.getChildren().setAll(text);

        ArrayList<Node> dNodes = new ArrayList<>();
        deckPane.getChildren().forEach(child -> {
            dNodes.add(child);
            if(child instanceof CardPane){
                Bounds nodeBounds = child.getLayoutBounds();
                child.setTranslateX(parentWidth / 2 - nodeBounds.getWidth() / 2);
                child.setTranslateY(parentHeight / 2 - nodeBounds.getHeight() / 2);
            }
        });
        deckPane.getChildren().setAll(dNodes);
    }

    private void updateCommanderPane(ResourceManager resourceManager){
        double parentWidth = (commanderPane.getWidth() > 0 ? commanderPane.getWidth() : commanderPane.getPrefWidth());
        double parentHeight = (commanderPane.getHeight() > 0 ? commanderPane.getHeight() : commanderPane.getPrefHeight());
        ArrayList<Node> cNodes = new ArrayList<>();
        commanderPane.getChildren().forEach(child -> {
            if(child instanceof CardPane){
                cNodes.add(child);
                Bounds nodeBounds = child.getLayoutBounds();
                child.setTranslateX(parentWidth / 2 - nodeBounds.getWidth() / 2);
                child.setTranslateY(parentHeight / 2 - nodeBounds.getHeight() / 2);
            }
        });
        commanderPane.getChildren().setAll(cNodes);
    }

    public void setPaneStyles(ResourceManager resourceManager){
        BackgroundFill backgroundFill = new BackgroundFill(resourceManager.getGuiPaneFillColor(), CornerRadii.EMPTY, new Insets(0));
        Background background = new Background(backgroundFill);
        BorderStroke borderStroke = new BorderStroke(resourceManager.getGuiPaneBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1));
        Border border = new Border(borderStroke);

        battlefieldPane.setBorder(border);
        exilePane.setBorder(border);
        handPane.setBorder(border);
        //deckGravePane.setBorder(border);
        playerPane.setBorder(border);
        commanderPane.setBorder(border);

        graveyardPane.setBorder(border);
        deckPane.setBorder(border);
        manaPane.setBorder(border);

        battlefieldPane.setBackground(background);
        exilePane.setBackground(background);
        handPane.setBackground(background);
        //deckGravePane.setBackground(background);
        playerPane.setBackground(background);
        commanderPane.setBackground(background);

        graveyardPane.setBackground(background);
        deckPane.setBackground(background);
        manaPane.setBackground(background);
    }

    public boolean released = true;
    public void updatePaneActions(ResourceManager resourceManager){ Rect selectRect = new Rect(0, 0, 0, 0);
        Pane selectArea = new Pane();
        selectArea.setVisible(false);
        selectArea.setBackground(new Background(new BackgroundFill(Color.rgb(77, 144, 254, 0.1), CornerRadii.EMPTY, new Insets(0))));
        selectArea.setBorder(new Border(new BorderStroke(Color.rgb(77, 144, 254), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        resourceManager.getScene().addEventHandler(KeyEvent.KEY_RELEASED, e -> released = true);
        resourceManager.getScene().addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(released) {
                released = false;
                if (e.getCode() == KeyCode.D && e.isControlDown()) {
                    CardEntity newCardEntity = new CardEntity(new CardData());
                    if (player.getDeck().size() > 0) {
                        newCardEntity.setCardData(player.getDeck().remove(player.getDeck().size() - 1));
                    }

                    player.getHand().add(newCardEntity.getCardData());
                    newCardEntity = new CardEntity(newCardEntity.getCardData());
                    CardPane newCardPane = CardPane.createCardPane(newCardEntity, resourceManager);
                    addSelectedChild(selectedNodes, newCardPane);
                    handPane.getChildren().add(newCardPane);

                    updatePanes(resourceManager);
                    updatePaneActions(resourceManager);
                }

                if (e.getCode() == KeyCode.CLOSE_BRACKET) {
                    if (e.isShiftDown()) {
                        for (CardPane card : selectedNodes) {
                            if (card.getCardEntity().getCardData().getType().toLowerCase().contains("planeswalker")) {
                                int loyalty = card.getCardEntity().getLoyaltyCounters();
                                card.setLoyaltyCounters(loyalty + 1);
                            } else {
                                int counters = card.getCardEntity().getPlusCounters();
                                card.setPlusCounters(counters + 1);
                            }
                        }
                    }
                } else if (e.getCode() == KeyCode.OPEN_BRACKET) {
                    if (e.isShiftDown()) {
                        for (CardPane card : selectedNodes) {
                            if (card.getCardEntity().getCardData().getType().toLowerCase().contains("planeswalker")) {
                                int loyalty = card.getCardEntity().getLoyaltyCounters();
                                if (loyalty > 0) {
                                    card.setLoyaltyCounters(loyalty - 1);
                                }
                            } else {
                                int counters = card.getCardEntity().getPlusCounters();
                                card.setPlusCounters(counters - 1);
                            }
                        }
                    }
                }
            }
        });

        parentGridPane.setOnMouseReleased(e -> {
            for(Drag drag : getDraggedNodes()){
                Pane dragParent = drag.dragParent; //(Pane) drag.drag.getParent();
                Pane srcParent = drag.srcParent; //(Pane) drag.src.getParent();

                dragParent.getChildren().remove(drag.drag);
                srcParent.getChildren().remove(drag.src);

                moveCardFromTo(resourceManager, srcParent, dragParent, drag.src, drag.drag);
            }

            removeAllDragged(getDraggedNodes());
            updatePanes(resourceManager);
        });

        for(Pane areaPane : areaPanes){
            areaPane.setOnMousePressed(e -> {
                boolean isChild = false;
                for(Node child : areaPane.getChildren()) {
                    if(child instanceof CardPane && child.getBoundsInParent().intersects(e.getX(), e.getY(), 1, 1)){
                        isChild = true;
                        break;
                    }
                }

                if(!isChild && e.getButton().equals(MouseButton.PRIMARY)) {
                    selectArea.setVisible(true);
                    selectRect.setZero();
                    selectRect.x1 = e.getX();
                    selectRect.y1 = e.getY();
                    selectArea.setTranslateX(e.getX());
                    selectArea.setTranslateY(e.getY());
                    selectArea.setMinWidth(0);
                    selectArea.setMinHeight(0);

                    if(areaPane.getChildren().contains(selectArea)){
                        areaPane.getChildren().remove(selectArea);
                    }
                    areaPane.getChildren().add(selectArea);
                }
            });

            areaPane.setOnMouseReleased(e -> {
                if (selectArea.getParent() != null && selectArea.isVisible() && e.getButton().equals(MouseButton.PRIMARY)) {
                    selectArea.setVisible(false);
                    areaPane.getChildren().remove(selectArea);

                    removeAllSelected(getSelectedNodes());
                    areaPane.getChildren().forEach(child -> {
                        if(child instanceof CardPane){
                            CardPane cardPane = (CardPane) child;
                            if (selectArea.getBoundsInParent().intersects(cardPane.getBoundsInParent())) {
                                if (!cardPane.isDragged()) {
                                    addSelectedChild(getSelectedNodes(), cardPane);
                                }
                            }
                        }
                    });
                }
            });

            areaPane.setOnMouseDragged(e -> {
                if (selectArea.getParent() != null && selectArea.isVisible() && e.getButton().equals(MouseButton.PRIMARY)) {
                    double parentWidth = ((Pane) selectArea.getParent()).getWidth();
                    double parentHeight = ((Pane) selectArea.getParent()).getHeight();

                    selectRect.x2 = e.getX();
                    selectRect.y2 = e.getY();

                    double minX = (selectRect.x1 < selectRect.x2 ? selectRect.x1 : selectRect.x2);
                    double maxX = (selectRect.x1 > selectRect.x2 ? selectRect.x1 : selectRect.x2);
                    double minY = (selectRect.y1 < selectRect.y2 ? selectRect.y1 : selectRect.y2);
                    double maxY = (selectRect.y1 > selectRect.y2 ? selectRect.y1 : selectRect.y2);

                    if (minX < 0) {
                        minX = 0;
                    } else if (maxX > parentWidth) {
                        maxX = parentWidth;
                    }

                    if (minY < 0) {
                        minY = 0;
                    } else if (maxY > parentHeight) {
                        maxY = parentHeight;
                    }

                    selectArea.setTranslateX(minX);
                    selectArea.setMinWidth(maxX - minX);

                    selectArea.setTranslateY(minY);
                    selectArea.setMinHeight(maxY - minY);
                }
            });

            for(Node child : areaPane.getChildren()){
                if(child instanceof CardPane) {
                    CardPane cardPane = (CardPane) child;
                    cardPane.setOnMouseEntered(e -> {
                        Image img = resourceManager.getCardImage(((CardPane) child).getCardEntity().getCardData(), 480, 680);
                        if (cardPane.getCardEntity().isFlipped() || img == null) {
                            img = resourceManager.getCardBackImage(480, 680);
                        }
                        resourceManager.getCardPreview().setImage(img);
                    });

                    cardPane.setOnMousePressed(e -> {
                        if (e.getButton().equals(MouseButton.PRIMARY)) {
                            if (getSelectedNodes().contains(cardPane)) {
                                if (e.isShiftDown()) {
                                    removeSelectedChild(getSelectedNodes(), cardPane);
                                }
                            } else {
                                if (!e.isShiftDown()) {
                                    removeAllSelected(getSelectedNodes());
                                }
                                addSelectedChild(getSelectedNodes(), cardPane);
                            }
                        }
                    });

                    cardPane.setOnDragDetected(e -> {
                        for (CardPane srcPane : getSelectedNodes()) {
                            CardPane newCardPane = CardPane.createCardPane(srcPane.getCardEntity(), resourceManager);
                            newCardPane.setTranslateX(cardPane.getTranslateX());
                            newCardPane.setTranslateY(cardPane.getTranslateY());

                            Drag drag = new Drag(srcPane, newCardPane, (Pane) srcPane.getParent(), (Pane) srcPane.getParent(), e.getX(), e.getY());
                            ((Pane) srcPane.getParent()).getChildren().add(drag.drag);

                            addDraggedChild(getDraggedNodes(), drag);
                        }
                    });

                    cardPane.setOnMouseDragged(e -> {
                        for (Drag drag : getDraggedNodes()) {

                            Pane dragParent = (Pane) drag.drag.getParent();
                            double mouseX = e.getSceneX();
                            double mouseY = e.getSceneY();

                            areaPanes.forEach(pane -> {
                                if (dragParent != pane && pane.localToScene(pane.getBoundsInLocal()).contains(mouseX, mouseY)) {
                                    dragParent.getChildren().remove(drag.drag);
                                    pane.getChildren().add(drag.drag);
                                    drag.dragParent = pane;
                                }
                            });

                            double sticky = 16;
                            Bounds parentBounds = dragParent.localToScene(dragParent.getBoundsInLocal());
                            Bounds childBounds = drag.drag.localToScene(drag.drag.getBoundsInLocal());

                            double globalX = mouseX - drag.xOffset + getDraggedNodes().indexOf(drag) * (childBounds.getWidth() + sticky);
                            double globalY = mouseY - drag.yOffset;

                            if (globalX < parentBounds.getMinX()) {
                                globalX = parentBounds.getMinX();
                            } else if (globalX + childBounds.getWidth() > parentBounds.getMaxX()) {
                                globalX = parentBounds.getMaxX() - childBounds.getWidth();
                            }

                            if (globalY < parentBounds.getMinY()) {
                                globalY = parentBounds.getMinY();
                            } else if (globalY + childBounds.getHeight() > parentBounds.getMaxY()) {
                                globalY = parentBounds.getMaxY() - childBounds.getHeight();
                            }

                            globalX = Math.floor(globalX / sticky) * sticky;
                            globalY = Math.floor(globalY / sticky) * sticky;

                            drag.drag.setTranslateX(globalX - parentBounds.getMinX());
                            drag.drag.setTranslateY(globalY - parentBounds.getMinY());

                            updatePanes(resourceManager);
                        }
                    });

                    cardPane.setOnMouseClicked(e -> {
                        if (e.isAltDown()) {
                            boolean isTapped = cardPane.getCardEntity().isTapped();
                            for(CardPane card : selectedNodes) {
                                card.setTapped(!isTapped);
                            }
                        } else if (e.getButton() == MouseButton.PRIMARY) {
                            if (e.getClickCount() >= 2) {
                                if (areaPane == deckPane) {
                                    deckPane.getChildren().remove(child);
                                    moveCardFromTo(resourceManager, deckPane, handPane, cardPane, CardPane.createCardPane(cardPane.getCardEntity(), resourceManager));
                                }

                                if (e.getClickCount() == 2) {
                                    if (areaPane == handPane) {
                                        handPane.getChildren().remove(child);
                                        moveCardFromTo(resourceManager, handPane, battlefieldPane, cardPane, CardPane.createCardPane(cardPane.getCardEntity(), resourceManager));
                                    }

                                    if (areaPane == commanderPane) {
                                        commanderPane.getChildren().remove(child);
                                        moveCardFromTo(resourceManager, commanderPane, battlefieldPane, cardPane, CardPane.createCardPane(cardPane.getCardEntity(), resourceManager));
                                    }

                                    if (areaPane == battlefieldPane) {
                                        boolean isTapped = cardPane.getCardEntity().isTapped();
                                        for(CardPane card : selectedNodes) {
                                            card.setTapped(!isTapped);
                                        }
                                    }
                                }
                            }

                            updatePanes(resourceManager);
                        }
                    });
                }
            }
        }
    }

    private void addSelectedChild(ArrayList<CardPane> selected, CardPane child){
        child.setSelected(true);
        selected.add(child);
    }

    private void removeSelectedChild(ArrayList<CardPane> selected, CardPane child){
        child.setSelected(false);
        if(selected.contains(child)) {
            selected.remove(child);
        }
    }

    private void removeAllSelected(ArrayList<CardPane> selected){
        ArrayList<CardPane> copy = new ArrayList<>();
        copy.addAll(selected);

        for(CardPane child : copy){
            removeSelectedChild(selected, child);
        }
    }

    private void addDraggedChild(ArrayList<Drag> dragged, Drag child){
        child.src.setDragged(true);
        dragged.add(child);
    }

    private void removeDraggedChild(ArrayList<Drag> dragged, Drag child){
        child.src.setDragged(false);
        if(dragged.contains(child)) {
            dragged.remove(child);
        }
    }

    private void removeAllDragged(ArrayList<Drag> dragged){
        ArrayList<Drag> copy = new ArrayList<>();
        copy.addAll(dragged);

        for(Drag child : copy){
            removeDraggedChild(dragged, child);
        }
    }

    //<editor-fold desc="Static Methods">
    public static PlayerArea createBottomPlayerArea(ResourceManager resourceManager, Player player){
        return createPlayerArea(resourceManager, player, 1);
    }
    public static PlayerArea createTopPlayerArea(ResourceManager resourceManager, Player player){
        return createPlayerArea(resourceManager, player, 0);
    }

    private static PlayerArea createPlayerArea(ResourceManager resourceManager, Player player, int handRowIndex){
        PlayerArea playerArea = new PlayerArea(player);

        // Parent Grid Pane - Add Panes
        playerArea.getParentGridPane().add(playerArea.getDeckGravePane(), 0, Math.abs(handRowIndex - 1));
        playerArea.getParentGridPane().add(playerArea.getBattlefieldPane(), 1, Math.abs(handRowIndex - 1));
        playerArea.getParentGridPane().add(playerArea.getExilePane(), 2, Math.abs(handRowIndex - 1));
        playerArea.getParentGridPane().add(playerArea.getPlayerPane(), 0, handRowIndex);
        playerArea.getParentGridPane().add(playerArea.getHandPane(), 1, handRowIndex);
        playerArea.getParentGridPane().add(playerArea.getCommanderPane(), 2, handRowIndex);

        // Parent Grid Pane - Set Scaling
        playerArea.getParentGridPane().setHgap(2);
        playerArea.getParentGridPane().setVgap(2);
        playerArea.getParentGridPane().getChildren().forEach(child -> {
            GridPane.setVgrow(child, Priority.ALWAYS);
            GridPane.setHgrow(child, Priority.ALWAYS);
        });

        // Parent Grid Pane - Set Sizes
        double initialWidth = CardPane.cardHeightAnchor*1.2*(resourceManager.getScene().getHeight()/1080);
        double initialDeckHeight = CardPane.cardHeightAnchor*1.2*(resourceManager.getScene().getHeight()/1080);
        playerArea.getParentGridPane().getColumnConstraints().add(0, new ColumnConstraints(initialWidth));
        playerArea.getParentGridPane().getColumnConstraints().add(1, new ColumnConstraints());
        playerArea.getParentGridPane().getColumnConstraints().add(2, new ColumnConstraints(initialWidth));
        playerArea.getParentGridPane().getRowConstraints().addAll(new RowConstraints(), new RowConstraints());
        playerArea.getParentGridPane().getRowConstraints().set(handRowIndex, new RowConstraints(initialDeckHeight));
        resourceManager.getScene().heightProperty().addListener(e -> {
            double width = CardPane.cardHeightAnchor*1.2*(resourceManager.getScene().getHeight()/1080);
            double height = CardPane.cardHeightAnchor*1.2*(resourceManager.getScene().getHeight()/1080);
            playerArea.getParentGridPane().getColumnConstraints().set(0, new ColumnConstraints(width));
            playerArea.getParentGridPane().getColumnConstraints().set(2, new ColumnConstraints(width));
            playerArea.getParentGridPane().getRowConstraints().set(handRowIndex, new RowConstraints(height));
        });

        // Deck Grave Pane - Add Panes
        playerArea.getDeckGravePane().add(playerArea.getGraveyardPane(), 0, (handRowIndex > 0 ? 0 : 2));
        playerArea.getDeckGravePane().add(playerArea.getDeckPane(), 0, 1);
        playerArea.getDeckGravePane().add(playerArea.getManaPane(), 0, handRowIndex*2);

        // Deck Grave Pane - Set Scaling
        playerArea.getDeckGravePane().setHgap(2);
        playerArea.getDeckGravePane().setVgap(2);
        playerArea.getDeckGravePane().getChildren().forEach(child -> {
            GridPane.setVgrow(child, Priority.ALWAYS);
            GridPane.setHgrow(child, Priority.ALWAYS);
        });

        // Deck Grave Pane - Set Sizes
        double initialHeight = CardPane.cardHeightAnchor*1.6*(resourceManager.getScene().getHeight()/1080);
        playerArea.getDeckGravePane().getRowConstraints().addAll(new RowConstraints(), new RowConstraints(initialHeight), new RowConstraints());
        playerArea.getDeckGravePane().getRowConstraints().set((handRowIndex > 0 ? 0 : 2), new RowConstraints(initialHeight));
        resourceManager.getScene().heightProperty().addListener(e -> {
            double height = CardPane.cardHeightAnchor*1.6*(resourceManager.getScene().getHeight()/1080);
            playerArea.getDeckGravePane().getRowConstraints().set(1, new RowConstraints(height));
            playerArea.getDeckGravePane().getRowConstraints().set((handRowIndex > 0 ? 0 : 2), new RowConstraints(height));
        });

        // Set Styles
        playerArea.setPaneStyles(resourceManager);

        // Resize
        playerArea.getDeckPane().setPrefSize(initialWidth, initialHeight);
        playerArea.getGraveyardPane().setPrefSize(initialWidth, initialHeight);
        playerArea.getBattlefieldPane().setPrefSize(playerArea.getParentGridPane().getWidth() - initialWidth*2, playerArea.getParentGridPane().getHeight() - initialDeckHeight);
        playerArea.getExilePane().setPrefSize(initialWidth, playerArea.getParentGridPane().getHeight() - initialDeckHeight);
        playerArea.getPlayerPane().setPrefSize(initialWidth, initialDeckHeight);
        playerArea.getHandPane().setPrefSize(playerArea.getParentGridPane().getWidth() - initialWidth*2, initialDeckHeight);
        playerArea.getCommanderPane().setPrefSize(initialWidth, initialDeckHeight);

        // Add Text Flow
        playerArea.getDeckPane().getChildren().add(playerArea.getDeckTextFlow());
        playerArea.getGraveyardPane().getChildren().add(playerArea.getGraveyardTextFlow());
        playerArea.getBattlefieldPane().getChildren().add(playerArea.getBattlefieldTextFlow());
        playerArea.getExilePane().getChildren().add(playerArea.getBattlefieldTextFlow());
        playerArea.getHandPane().getChildren().add(playerArea.getHandTextFlow());
        playerArea.getCommanderPane().getChildren().add(playerArea.getCommanderTextFlow());

        // Add area panes
        playerArea.getAreaPanes().add(playerArea.getBattlefieldPane());
        playerArea.getAreaPanes().add(playerArea.getExilePane());
        playerArea.getAreaPanes().add(playerArea.getHandPane());
        playerArea.getAreaPanes().add(playerArea.getDeckPane());
        playerArea.getAreaPanes().add(playerArea.getGraveyardPane());
        playerArea.getAreaPanes().add(playerArea.getCommanderPane());

        return playerArea;
    }
    //</editor-fold>
}

class Drag {
    Pane srcParent;
    CardPane src;
    Pane dragParent;
    CardPane drag;
    double xOffset, yOffset;
    public Drag(CardPane src, CardPane drag, Pane srcParent, Pane dragParent, double xOffset, double yOffset){
        this.srcParent = srcParent;
        this.dragParent = dragParent;
        this.src = src;
        this.drag = drag;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
}

class Rect {
    double x1, y1, x2, y2;
    public Rect(double x1, double y1, double x2, double y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    public void setZero(){
        x1 = y1 = x2 = y2 = 0;
    }
}