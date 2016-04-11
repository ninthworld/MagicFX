package org.ninthworld.magicfx;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by NinthWorld on 4/8/2016.
 */
public class PlayerArea {

    private GridPane parentGridPane, deckGravePane;
    private Pane battlefieldPane, exilePane, handPane, playerPane, commanderPane, graveyardPane, deckPane, manaPane;

    private ArrayList<Pane> areaPanes;
    private ArrayList<Node> selectedNodes;
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

        this.areaPanes = new ArrayList<>();
        this.selectedNodes = new ArrayList<>();
        this.draggedNodes = new ArrayList<>();
    }

    //<editor-fold desc="Getters and Setters">
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

    public ArrayList<Node> getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(ArrayList<Node> selectedNodes) {
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

        updatePanes();
        updatePaneActions(resourceManager);
    }

    public void updatePanes(){
        updateBattlefieldPane();
        updateHandPane();
        updateExilePane();
        updateDeckPane();
        updateGraveyardPane();
        updateCommanderPane();
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

            graveyardPane.getChildren().clear();
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

    private void updateBattlefieldPane(){
        double parentWidth = (battlefieldPane.getWidth() > 0 ? battlefieldPane.getWidth() : battlefieldPane.getPrefWidth());
        double parentHeight = (battlefieldPane.getHeight() > 0 ? battlefieldPane.getHeight() : battlefieldPane.getPrefHeight());
        ArrayList<Node> bNodes = new ArrayList<>();
        battlefieldPane.getChildren().forEach(bNodes::add);
        Collections.sort(bNodes, (n1, n2) -> {
            if(n1.getTranslateY() < n2.getTranslateY() || n1.getTranslateX() < n2.getTranslateX()){
                return -1;
            }
            return 0;
        });
        battlefieldPane.getChildren().setAll(bNodes);
    }

    private void updateHandPane(){
        double parentWidth = (handPane.getWidth() > 0 ? handPane.getWidth() : handPane.getPrefWidth());
        double parentHeight = (handPane.getHeight() > 0 ? handPane.getHeight() : handPane.getPrefHeight());
        ArrayList<Node> hNodes = new ArrayList<>();
        handPane.getChildren().forEach(hNodes::add);
        Collections.sort(hNodes, (n1, n2) -> {
            if(n1.getTranslateX() < n2.getTranslateX()){
                return -1;
            }
            return 0;
        });

        hNodes.forEach(node -> {
            Bounds nodeBounds = node.getBoundsInParent();
            double padding = 8;
            if(hNodes.size()*(nodeBounds.getWidth()+padding) > parentWidth){
                padding -= (hNodes.size()*(nodeBounds.getWidth()+padding) - parentWidth + nodeBounds.getWidth()/2)/hNodes.size();
            }
            node.setTranslateX(parentWidth/2 - ((nodeBounds.getWidth() + padding)*hNodes.size())/2 + (nodeBounds.getWidth() + padding)*hNodes.indexOf(node));
            node.setTranslateY(parentHeight/2 - nodeBounds.getHeight()/2);
        });
    }

    private void updateExilePane(){
        double parentWidth = (exilePane.getWidth() > 0 ? exilePane.getWidth() : exilePane.getPrefWidth());
        double parentHeight = (exilePane.getHeight() > 0 ? exilePane.getHeight() : exilePane.getPrefHeight());
        ArrayList<Node> eNodes = new ArrayList<>();
        exilePane.getChildren().forEach(eNodes::add);
        Collections.sort(eNodes, (n1, n2) -> {
            if(n1.getTranslateY() < n2.getTranslateY()){
                return -1;
            }
            return 0;
        });
        exilePane.getChildren().setAll(eNodes);

        eNodes.forEach(node -> {
            Bounds nodeBounds = node.getBoundsInParent();
            node.setTranslateX(parentWidth/2 - nodeBounds.getWidth()/2);
        });
    }

    private void updateGraveyardPane(){
        double parentWidth = (graveyardPane.getWidth() > 0 ? graveyardPane.getWidth() : graveyardPane.getPrefWidth());
        double parentHeight = (graveyardPane.getHeight() > 0 ? graveyardPane.getHeight() : graveyardPane.getPrefHeight());
        graveyardPane.getChildren().forEach(node -> {
            Bounds nodeBounds = node.getBoundsInParent();
            node.setTranslateX(parentWidth/2 - nodeBounds.getWidth()/2);
            node.setTranslateY(parentHeight/2 - nodeBounds.getHeight()/2);
        });
    }

    private void updateDeckPane(){
        double parentWidth = (deckPane.getWidth() > 0 ? deckPane.getWidth() : deckPane.getPrefWidth());
        double parentHeight = (deckPane.getHeight() > 0 ? deckPane.getHeight() : deckPane.getPrefHeight());
        deckPane.getChildren().forEach(node -> {
            Bounds nodeBounds = node.getBoundsInParent();
            node.setTranslateX(parentWidth/2 - nodeBounds.getWidth()/2);
            node.setTranslateY(parentHeight/2 - nodeBounds.getHeight()/2);
        });
    }

    private void updateCommanderPane(){
        double parentWidth = (commanderPane.getWidth() > 0 ? commanderPane.getWidth() : commanderPane.getPrefWidth());
        double parentHeight = (commanderPane.getHeight() > 0 ? commanderPane.getHeight() : commanderPane.getPrefHeight());
        commanderPane.getChildren().forEach(node -> {
            Bounds nodeBounds = node.getBoundsInParent();
            node.setTranslateX(parentWidth/2 - nodeBounds.getWidth()/2);
            node.setTranslateY(parentHeight/2 - nodeBounds.getHeight()/2);
        });
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

    public void updatePaneActions(ResourceManager resourceManager){
        for(Pane areaPane : areaPanes){
            Rect selectRect = new Rect(0, 0, 0, 0);
            Pane selectArea = new Pane();
            selectArea.setVisible(false);
            selectArea.setBackground(new Background(new BackgroundFill(Color.rgb(77, 144, 254, 0.1), CornerRadii.EMPTY, new Insets(0))));
            selectArea.setBorder(new Border(new BorderStroke(Color.rgb(77, 144, 254), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

            areaPane.setOnMousePressed(e -> {
                boolean isChild = false;
                for(Node child : areaPane.getChildren()) {
                    if(child.getBoundsInParent().intersects(e.getX(), e.getY(), 1, 1)){
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
                    areaPane.getChildren().add(selectArea);
                }
            });

            areaPane.setOnMouseDragged(e -> {
                if (selectArea.isVisible() && e.getButton().equals(MouseButton.PRIMARY)) {
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

            areaPane.setOnMouseReleased(e -> {
                if (selectArea.isVisible() && e.getButton().equals(MouseButton.PRIMARY)) {
                    selectArea.setVisible(false);
                    areaPane.getChildren().remove(selectArea);

                    removeAllSelected(getSelectedNodes());
                    areaPane.getChildren().forEach(child -> {
                        if (selectArea.getBoundsInParent().intersects(child.getBoundsInParent())) {
                            addSelectedChild(getSelectedNodes(), child);
                        }
                    });
                }
            });

            for(Node child : areaPane.getChildren()){
                child.setOnMouseEntered(e -> {
                    if(child instanceof CardPane){
                        CardPane cardPane = (CardPane) child;
                        Image img = resourceManager.getCardImage(((CardPane) child).getCardEntity().getCardData());
                        if(cardPane.getCardEntity().isFlipped() || img == null){
                            img = resourceManager.getCardBackImage();
                        }
                        resourceManager.getCardPreview().setImage(img);
                    }
                });

                child.setOnMousePressed(e -> {
                    if(e.getButton().equals(MouseButton.PRIMARY)) {
                        if (getSelectedNodes().contains(child)) {
                            if (e.isShiftDown()) {
                                removeSelectedChild(getSelectedNodes(), child);
                            }
                        } else {
                            if (!e.isShiftDown()) {
                                removeAllSelected(getSelectedNodes());
                            }
                            addSelectedChild(getSelectedNodes(), child);
                        }
                    }
                });

                child.setOnDragDetected(e -> {
                    for(Node select : getSelectedNodes()){
                        if(select instanceof CardPane){
                            CardPane srcPane = (CardPane) select;
                            CardPane cardPane = CardPane.createCardPane(srcPane.getCardEntity(), resourceManager);
                            cardPane.setTranslateX(((CardPane) child).getTranslateX());
                            cardPane.setTranslateY(((CardPane) child).getTranslateY());

                            Drag drag = new Drag(srcPane, cardPane, (Pane) srcPane.getParent(), (Pane) srcPane.getParent(), e.getX(), e.getY());
                            ((Pane) select.getParent()).getChildren().add(drag.drag);

                            addDraggedChild(getDraggedNodes(), drag);
                        }
                    }
                });

                child.setOnMouseDragged(e -> {
                    for(Drag drag : getDraggedNodes()){
                        Pane dragParent = (Pane) drag.drag.getParent();
                        double mouseX = e.getSceneX();
                        double mouseY = e.getSceneY();

                        areaPanes.forEach(pane -> {
                            if (drag.drag.getParent() != pane && pane.localToScene(pane.getBoundsInLocal()).contains(mouseX, mouseY)) {
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

                        updatePanes();
                    }
                });

                child.setOnMouseReleased(e -> {
                    for(Drag drag : getDraggedNodes()){
                        Pane dragParent = drag.dragParent; //(Pane) drag.drag.getParent();
                        Pane srcParent = drag.srcParent; //(Pane) drag.src.getParent();

                        dragParent.getChildren().remove(drag.drag);
                        srcParent.getChildren().remove(drag.src);

                        moveCardFromTo(resourceManager, srcParent, dragParent, drag.src, drag.drag);
                    }

                    removeAllDragged(getDraggedNodes());
                    updatePanes();
                });

                child.setOnMouseClicked(e -> {
                    if(e.getButton() == MouseButton.PRIMARY){
                        if(e.getClickCount() == 2){
                            CardPane cardPane = (CardPane) child;
                            if(areaPane == deckPane){
                                deckPane.getChildren().remove(child);
                                moveCardFromTo(resourceManager, deckPane, handPane, cardPane, CardPane.createCardPane(cardPane.getCardEntity(), resourceManager));
                            }

                            if(areaPane == handPane){
                                handPane.getChildren().remove(child);
                                moveCardFromTo(resourceManager, handPane, battlefieldPane, cardPane, CardPane.createCardPane(cardPane.getCardEntity(), resourceManager));
                            }

                            if(areaPane == commanderPane){
                                commanderPane.getChildren().remove(child);
                                moveCardFromTo(resourceManager, commanderPane, battlefieldPane, cardPane, CardPane.createCardPane(cardPane.getCardEntity(), resourceManager));
                            }

                            if(areaPane == battlefieldPane){
                                cardPane.setTapped(cardPane.getCardEntity().isTapped());
                            }

                            updatePanes();
                        }
                    }
                });
            }
        }
    }

    private void addSelectedChild(ArrayList<Node> selected, Node child){
       ((CardPane) child).setSelected(true);
        selected.add(child);
    }

    private void removeSelectedChild(ArrayList<Node> selected, Node child){
        ((CardPane) child).setSelected(false);
        selected.remove(child);
    }

    private void removeAllSelected(ArrayList<Node> selected){
        ArrayList<Node> copy = new ArrayList<>();
        copy.addAll(selected);

        for(Node child : copy){
            removeSelectedChild(selected, child);
        }
    }

    private void addDraggedChild(ArrayList<Drag> dragged, Drag child){
        child.src.setDragged(true);
        dragged.add(child);
    }

    private void removeDraggedChild(ArrayList<Drag> dragged, Drag child){
        child.src.setDragged(false);
        dragged.remove(child);
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