package org.ninthworld.magicfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.smartcardio.Card;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Main extends Application {

    private ResourceManager resourceManager;

    private Scene scene;
    private BorderPane globalBorderPane;
    private GridPane globalGridPane, playGridPane, sideGridPane;
    private SplitPane globalSplitTop, globalSplitBottom;

    private ImageView cardPreview;

    private Player mainPlayer;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/magicfx.fxml"));
        primaryStage.setTitle("MagicFX");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();
        scene = primaryStage.getScene();

        resourceManager = new ResourceManager(scene);
        resourceManager.load();

        globalBorderPane = (BorderPane) scene.lookup("#globalBorderPane");
        globalBorderPane.setBackground(
                new Background(
                        new BackgroundImage(
                                resourceManager.getBackgroundImage(),
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER,
                                new BackgroundSize(1, 1, true, true, true, true)
                        )
                )
        );

        globalGridPane = (GridPane) scene.lookup("#globalGridPane");
        playGridPane = (GridPane) scene.lookup("#playGridPane");
        sideGridPane = (GridPane) scene.lookup("#sideGridPane");
        globalSplitTop = (SplitPane) scene.lookup("#globalSplitTop");
        globalSplitBottom = (SplitPane) scene.lookup("#globalSplitBottom");
        globalSplitTop.setBackground(null);
        globalSplitBottom.setBackground(null);

        cardPreview = (ImageView) scene.lookup("#cardPreview");
        cardPreview.setFitWidth(Math.min(312, scene.getWidth()-scene.getHeight()));
        cardPreview.setPreserveRatio(true);
        cardPreview.setSmooth(true);
        cardPreview.setImage(resourceManager.getCardBackImage());
        resourceManager.setCardPreview(cardPreview);

        sideGridPane.getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), new RowConstraints());
        sideGridPane.getRowConstraints().get(0).setPrefHeight(cardPreview.getFitWidth()*(cardPreview.getImage().getHeight()/cardPreview.getImage().getWidth()));

        globalGridPane.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints());
        globalGridPane.getColumnConstraints().get(0).setMinWidth(scene.getHeight());
        globalGridPane.getColumnConstraints().get(1).setMaxWidth(312);

        scene.heightProperty().addListener(e -> {
            globalGridPane.getColumnConstraints().get(0).setMinWidth(scene.getHeight());
            cardPreview.setFitWidth(Math.min(312, scene.getWidth()-scene.getHeight()));
            sideGridPane.getRowConstraints().get(0).setMaxHeight(cardPreview.getFitWidth()*(cardPreview.getImage().getHeight()/cardPreview.getImage().getWidth()));
        });
        scene.widthProperty().addListener(e -> {
            globalGridPane.getColumnConstraints().get(0).setMinWidth(scene.getHeight());
            cardPreview.setFitWidth(Math.min(312, scene.getWidth()-scene.getHeight()));
            sideGridPane.getRowConstraints().get(0).setMaxHeight(cardPreview.getFitWidth()*(cardPreview.getImage().getHeight()/cardPreview.getImage().getWidth()));
        });

        mainPlayer = new Player();
        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\NinthWorld\\IdeaProjects\\MagicFX"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Supported Formats", "*.dec; *.deck; *.jdeck"));
        File deckFile = fileChooser.showOpenDialog(primaryStage);*/
        File deckFile = new File("EDH-Simic-Vorel.jdeck");
        mainPlayer.setDeckData(DeckLoader.loadDeckFile(deckFile, resourceManager.getAllCards()));
        mainPlayer.initGame();

        PlayerArea playerArea = PlayerArea.createBottomPlayerArea(resourceManager, mainPlayer);
        globalSplitBottom.getItems().add(playerArea.getParentGridPane());
        playerArea.initAreas(resourceManager);
        playerArea.updatePaneActions(resourceManager);

        PlayerArea playerArea2 = PlayerArea.createTopPlayerArea(resourceManager, new Player());
        globalSplitTop.getItems().add(playerArea2.getParentGridPane());

        scene.widthProperty().addListener(e -> {
            playerArea.updatePanes();
            playerArea2.updatePanes();
        });
        scene.heightProperty().addListener(e -> {
            playerArea.updatePanes();
            playerArea2.updatePanes();
        });
        playerArea.updatePaneActions(resourceManager);
    }

    public static void main(String[] args) {
        launch(args);
    }
}