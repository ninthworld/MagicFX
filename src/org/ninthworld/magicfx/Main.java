package org.ninthworld.magicfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Main extends Application {

    private Color paneBorderColor, paneFillColor;
    private Scene scene;
    private BorderPane globalBorderPane;
    private GridPane globalGridPane, topGridPane, bottomGridPane;
    private Pane mainBattlefieldPane, mainExilePane, mainHandPane, mainDeckGravePane, mainPlayerPane, mainCommanderPane;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/magicfx.fxml"));
        primaryStage.setTitle("MagicFX");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();
        scene = primaryStage.getScene();

        Image bg = new Image(getClass().getResource("/bg/bg5.jpg").toString());
        globalBorderPane = (BorderPane) scene.lookup("#globalBorderPane");
        globalBorderPane.setBackground(new Background(new BackgroundImage(bg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, true, true))));

        paneBorderColor = Color.rgb(255, 255, 255, 0.8);
        paneFillColor = Color.rgb(255, 255, 255, 0.2);

        globalGridPane = (GridPane) scene.lookup("#globalGridPane");
        topGridPane = (GridPane) scene.lookup("#topGridPane");
        bottomGridPane = (GridPane) scene.lookup("#bottomGridPane");

        mainBattlefieldPane = (Pane) scene.lookup("#mainBattlefieldPane");
        mainExilePane = (Pane) scene.lookup("#mainExilePane");
        mainHandPane = (Pane) scene.lookup("#mainHandPane");
        mainDeckGravePane = (Pane) scene.lookup("#mainDeckGravePane");
        mainPlayerPane = (Pane) scene.lookup("#mainPlayerPane");
        mainCommanderPane = (Pane) scene.lookup("#mainCommanderPane");

        mainBattlefieldPane.setBorder(new Border(new BorderStroke(paneBorderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        mainExilePane.setBorder(new Border(new BorderStroke(paneBorderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        mainHandPane.setBorder(new Border(new BorderStroke(paneBorderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        mainDeckGravePane.setBorder(new Border(new BorderStroke(paneBorderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        mainPlayerPane.setBorder(new Border(new BorderStroke(paneBorderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        mainCommanderPane.setBorder(new Border(new BorderStroke(paneBorderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        mainBattlefieldPane.setBackground(new Background(new BackgroundFill(paneFillColor, CornerRadii.EMPTY, new Insets(0))));
        mainExilePane.setBackground(new Background(new BackgroundFill(paneFillColor, CornerRadii.EMPTY, new Insets(0))));
        mainHandPane.setBackground(new Background(new BackgroundFill(paneFillColor, CornerRadii.EMPTY, new Insets(0))));
        mainDeckGravePane.setBackground(new Background(new BackgroundFill(paneFillColor, CornerRadii.EMPTY, new Insets(0))));
        mainPlayerPane.setBackground(new Background(new BackgroundFill(paneFillColor, CornerRadii.EMPTY, new Insets(0))));
        mainCommanderPane.setBackground(new Background(new BackgroundFill(paneFillColor, CornerRadii.EMPTY, new Insets(0))));

        mainBattlefieldPane.getChildren().add(createCardBorderPane());
        mainExilePane.getChildren().add(createCardBorderPane());

        ArrayList<Node> selected = new ArrayList<>();
        ArrayList<Drag> dragged = new ArrayList<>();

        HashMap<Pane, AreaPaneType> areaPaneTypes = new HashMap<>();
        ArrayList<Pane> areaPanes = new ArrayList<>();

        areaPanes.add(mainBattlefieldPane);
        areaPaneTypes.put(mainBattlefieldPane, AreaPaneType.Battlefield);

        areaPanes.add(mainExilePane);
        areaPaneTypes.put(mainExilePane, AreaPaneType.Exile);

        areaPanes.add(mainHandPane);
        areaPaneTypes.put(mainHandPane, AreaPaneType.Hand);

        scene.widthProperty().addListener(e -> areaPanes.forEach(pane -> updateAreaPane(pane, areaPaneTypes.get(pane))));
        scene.heightProperty().addListener(e -> areaPanes.forEach(pane -> updateAreaPane(pane, areaPaneTypes.get(pane))));

        areaPanes.forEach(areaPane -> {
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

                if(!isChild) {
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
                if(selectArea.isVisible()){
                    double parentWidth = ((Pane) selectArea.getParent()).getWidth();
                    double parentHeight = ((Pane) selectArea.getParent()).getHeight();

                    selectRect.x2 = e.getX();
                    selectRect.y2 = e.getY();

                    double minX = (selectRect.x1 < selectRect.x2 ? selectRect.x1 : selectRect.x2);
                    double maxX = (selectRect.x1 > selectRect.x2 ? selectRect.x1 : selectRect.x2);
                    double minY = (selectRect.y1 < selectRect.y2 ? selectRect.y1 : selectRect.y2);
                    double maxY = (selectRect.y1 > selectRect.y2 ? selectRect.y1 : selectRect.y2);

                    if(minX < 0){
                        minX = 0;
                    }else if(maxX > parentWidth){
                        maxX = parentWidth;
                    }

                    if(minY < 0){
                        minY = 0;
                    }else if(maxY > parentHeight){
                        maxY = parentHeight;
                    }

                    selectArea.setTranslateX(minX);
                    selectArea.setMinWidth(maxX - minX);

                    selectArea.setTranslateY(minY);
                    selectArea.setMinHeight(maxY - minY);
                }
            });

            areaPane.setOnMouseReleased(e -> {
                if(selectArea.isVisible()){
                    selectArea.setVisible(false);
                    areaPane.getChildren().remove(selectArea);

                    removeAllSelected(selected);
                    areaPane.getChildren().forEach(child -> {
                        if(selectArea.getBoundsInParent().intersects(child.getBoundsInParent())){
                            addSelectedChild(selected, child);
                        }
                    });
                }
            });

            areaPane.getChildren().forEach(child -> {
                child.setOnMousePressed(e -> {
                    if(selected.contains(child)){
                        if(e.isShiftDown()){
                            removeSelectedChild(selected, child);
                        }
                    }else{
                        if(!e.isShiftDown()){
                            removeAllSelected(selected);
                        }
                        addSelectedChild(selected, child);
                    }
                });

                child.setOnDragDetected(e -> {
                    selected.forEach(select -> {
                        BorderPane cardPane = createCardBorderPane();
                        cardPane.setTranslateX(((BorderPane) child).getTranslateX());
                        cardPane.setTranslateY(((BorderPane) child).getTranslateY());

                        Drag drag = new Drag(select, cardPane, e.getX(), e.getY());
                        ((Pane) select.getParent()).getChildren().add(drag.drag);

                        addDraggedChild(dragged, drag);
                    });
                });

                child.setOnMouseDragged(e -> {
                    dragged.forEach(drag -> {
                        Pane dragParent = (Pane) drag.drag.getParent();
                        double mouseX = e.getSceneX();
                        double mouseY = e.getSceneY();

                        areaPanes.forEach(pane -> {
                            if(drag.drag.getParent() != pane && pane.localToScene(pane.getBoundsInLocal()).contains(mouseX, mouseY)){
                                dragParent.getChildren().remove(drag.drag);
                                pane.getChildren().add(drag.drag);
                            }
                        });

                        double sticky = 16;
                        Bounds parentBounds = dragParent.localToScene(dragParent.getBoundsInLocal());
                        Bounds childBounds = drag.drag.localToScene(drag.drag.getBoundsInLocal());

                        double globalX = mouseX - drag.xOffset + dragged.indexOf(drag) * childBounds.getWidth();
                        double globalY = mouseY - drag.yOffset;

                        if(globalX < parentBounds.getMinX()){
                            globalX = parentBounds.getMinX();
                        }else if(globalX + childBounds.getWidth() > parentBounds.getMaxX()){
                            globalX = parentBounds.getMaxX() - childBounds.getWidth();
                        }

                        if(globalY < parentBounds.getMinY()){
                            globalY = parentBounds.getMinY();
                        }else if(globalY + childBounds.getHeight() > parentBounds.getMaxY()){
                            globalY = parentBounds.getMaxY() - childBounds.getHeight();
                        }

                        globalX = Math.floor(globalX / sticky) * sticky;
                        globalY = Math.floor(globalY / sticky) * sticky;

                        drag.drag.setTranslateX(globalX - parentBounds.getMinX());
                        drag.drag.setTranslateY(globalY - parentBounds.getMinY());

                        areaPanes.forEach(pane -> updateAreaPane(pane, areaPaneTypes.get(pane)));
                    });
                });

                child.setOnMouseReleased(e -> {
                    dragged.forEach(drag -> {
                        Pane dragParent = (Pane) drag.drag.getParent();
                        Pane srcParent = (Pane) drag.src.getParent();

                        dragParent.getChildren().remove(drag.drag);
                        if(dragParent != srcParent){
                            srcParent.getChildren().remove(drag.src);
                            dragParent.getChildren().add(drag.src);
                        }

                        drag.src.setTranslateX(drag.drag.getTranslateX());
                        drag.src.setTranslateY(drag.drag.getTranslateY());
                    });
                    removeAllDragged(dragged);

                    areaPanes.forEach(pane -> updateAreaPane(pane, areaPaneTypes.get(pane)));
                });
            });
        });
    }

    private void updateAreaPane(Pane pane, AreaPaneType paneType){
        switch(paneType){
            case Battlefield:
                ArrayList<Node> bNodes = new ArrayList<>();
                pane.getChildren().forEach(bNodes::add);
                Collections.sort(bNodes, (n1, n2) -> {
                    if(n1.getTranslateY() < n2.getTranslateY() || n1.getTranslateX() < n2.getTranslateX()){
                        return -1;
                    }
                    return 0;
                });
                pane.getChildren().setAll(bNodes);
                break;
            case Hand:
                ArrayList<Node> hNodes = new ArrayList<>();
                pane.getChildren().forEach(hNodes::add);
                Collections.sort(hNodes, (n1, n2) -> {
                    if(n1.getTranslateX() < n2.getTranslateX()){
                        return -1;
                    }
                    return 0;
                });

                hNodes.forEach(node -> {
                    Bounds nodeBounds = node.getBoundsInParent();
                    double padding = 8;
                    node.setTranslateX(pane.getWidth()/2 - ((nodeBounds.getWidth() + padding)*hNodes.size())/2 + (nodeBounds.getWidth() + padding)*hNodes.indexOf(node));
                    node.setTranslateY(pane.getHeight()/2 - nodeBounds.getHeight()/2);
                });

                break;
            case Exile:
                break;
        }
    }

    private void addSelectedChild(ArrayList<Node> selected, Node child){
        ((BorderPane) child).setBorder(new Border(new BorderStroke(Color.rgb(77, 144, 254), BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(2))));
        selected.add(child);
    }

    private void removeSelectedChild(ArrayList<Node> selected, Node child){
        ((BorderPane) child).setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(2))));
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
        child.src.setEffect(new ColorAdjust(0, 0, -0.5, 0));
        dragged.add(child);
    }

    private void removeDraggedChild(ArrayList<Drag> dragged, Drag child){
        child.src.setEffect(null);
        dragged.remove(child);
    }

    private void removeAllDragged(ArrayList<Drag> dragged){
        ArrayList<Drag> copy = new ArrayList<>();
        copy.addAll(dragged);

        for(Drag child : copy){
            removeDraggedChild(dragged, child);
        }
    }

    private BorderPane createCardBorderPane(){
        BorderPane cardPane = new BorderPane();
        cardPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(2))));

        Image img = new Image(new File("Progenitor Mimic.full.jpg").toURI().toString());
        ImageView imgView = new ImageView();
        imgView.setImage(img);
        imgView.setFitHeight(68);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setCache(true);

        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.setRadius(1.6);
        imgView.setEffect(gaussianBlur);

        cardPane.setMaxWidth(imgView.getFitWidth());
        cardPane.setMaxHeight(imgView.getFitHeight());
        cardPane.setCenter(imgView);

        return cardPane;
    }


    public static void main(String[] args) {
        launch(args);
    }
}

class Drag {
    Node src;
    Node drag;
    double xOffset, yOffset;
    public Drag(Node src, Node drag, double xOffset, double yOffset){
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

enum AreaPaneType {
    Battlefield, Exile, Hand;
}