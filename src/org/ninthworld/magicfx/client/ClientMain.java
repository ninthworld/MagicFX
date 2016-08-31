package org.ninthworld.magicfx.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ninthworld.magicfx.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ClientMain extends Application {

    private ResourceManager resourceManager;

    private Stage primaryStage;
    private Scene scene;
    private BorderPane globalBorderPane;
    private GridPane globalGridPane, playGridPane, sideGridPane;
    private SplitPane globalSplitTop, globalSplitBottom;

    private ImageView cardPreview;
    private ChoiceBox gameFormatChoiceBox, gameModeChoiceBox;
    private Button gameStartBtn, gameEndBtn, chatSendBtn;
    private TextArea chatTextArea, eventLogTextArea;
    private TextField chatTextField;
    private ListView connectedListView;

    private GameSettings clientGameSettings;
    private ClientThread clientThread;
    private ClientGameManager clientGameManager = null;
    //private Player mainPlayer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/magicfx.fxml"));
        primaryStage.setTitle("MagicFX");
        double windowWidth = .8* Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double windowHeight = .8*Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        primaryStage.setScene(new Scene(root, windowWidth, windowHeight));
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
        cardPreview.setImage(resourceManager.getCardBackImage(480, 680));
        resourceManager.setCardPreview(cardPreview);

        sideGridPane.getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), new RowConstraints());
        sideGridPane.getRowConstraints().get(0).setPrefHeight(cardPreview.getFitWidth()*(cardPreview.getImage().getHeight()/cardPreview.getImage().getWidth()));

        globalGridPane.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints());
        globalGridPane.getColumnConstraints().get(0).setMinWidth(scene.getHeight());
        globalGridPane.getColumnConstraints().get(1).setMaxWidth(312);

        eventLogTextArea = (TextArea) scene.lookup("#eventLogTextArea");
        chatSendBtn = (Button) scene.lookup("#chatSendBtn");
        chatTextArea = (TextArea) scene.lookup("#chatTextArea");
        chatTextField = (TextField) scene.lookup("#chatTextField");
        gameStartBtn = (Button) scene.lookup("#gameStartBtn");
        gameEndBtn = (Button) scene.lookup("#gameEndBtn");
        gameFormatChoiceBox = (ChoiceBox) scene.lookup("#gameFormatChoiceBox");
        gameModeChoiceBox = (ChoiceBox) scene.lookup("#gameModeChoiceBox");
        gameFormatChoiceBox.getItems().setAll("Standard", "Commander");
        gameModeChoiceBox.getItems().setAll("FFA", "Teams");
        gameFormatChoiceBox.getSelectionModel().select(0);
        gameModeChoiceBox.getSelectionModel().select(0);

        connectedListView = (ListView) scene.lookup("#connectedListView");
        connectedListView.setPrefWidth(cardPreview.getFitWidth());

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

        clientGameSettings = new GameSettings();
        clientThread = ClientThread.createConnection(this, getParameters());
        clientThread.start();
        primaryStage.setOnCloseRequest(e -> {
            clientThread.setRunning(false);
        });

        chatTextField.setOnAction(e -> {
            try {
                PacketHandler.sendChatMessage(clientThread, chatTextField.getText());
                chatTextField.setText("");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        chatSendBtn.setOnMouseClicked(e -> {
            try {
                PacketHandler.sendChatMessage(clientThread, chatTextField.getText());
                chatTextField.setText("");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        /*mainPlayer = new Player();
        //FileChooser fileChooser = new FileChooser();
        //fileChooser.setInitialDirectory(new File("C:\\Users\\NinthWorld\\IdeaProjects\\MagicFX"));
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Supported Formats", "*.dec; *.deck; *.jdeck"));
        //File deckFile = fileChooser.showOpenDialog(primaryStage);
        File deckFile = new File("EDH-Boros-Aurelia.jdeck");
        mainPlayer.setDeckData(DeckLoader.loadDeckFile(deckFile, resourceManager.getAllCards()));
        mainPlayer.initGame();

        PlayerArea playerArea = PlayerArea.createBottomPlayerArea(resourceManager, mainPlayer);
        globalSplitBottom.getItems().add(playerArea.getParentGridPane());
        playerArea.initAreas(resourceManager);
        playerArea.updatePaneActions(resourceManager);

        PlayerArea playerArea2 = PlayerArea.createTopPlayerArea(resourceManager, new Player());
        globalSplitTop.getItems().add(playerArea2.getParentGridPane());

        scene.widthProperty().addListener(e -> {
            playerArea.updatePanes(resourceManager);
            playerArea2.updatePanes(resourceManager);
        });
        scene.heightProperty().addListener(e -> {
            playerArea.updatePanes(resourceManager);
            playerArea2.updatePanes(resourceManager);
        });
        playerArea.updatePaneActions(resourceManager);*/
    }

    public static void main(String[] args) {
        launch(args);
    }

    public SplitPane getGlobalSplitTop(){
        return globalSplitTop;
    }

    public SplitPane getGlobalSplitBottom(){
        return globalSplitBottom;
    }

    public ClientThread getClientThread(){
        return clientThread;
    }

    public GameSettings getClientGameSettings(){
        return clientGameSettings;
    }

    public ResourceManager getResourceManager(){
        return resourceManager;
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public void addChatMessage(String username, String message){
        String newText = chatTextArea.getText().concat("[" + username + "]: " + message + "\n");
        chatTextArea.setText(newText);
    }

    public void updateLobby(JSONArray members, JSONObject gameSettings, String lobbyLeader, boolean gameRunning){
        gameFormatChoiceBox.getSelectionModel().select(((Long) gameSettings.get("gameFormat")).intValue());
        gameModeChoiceBox.getSelectionModel().select(((Long) gameSettings.get("gameMode")).intValue());

        gameFormatChoiceBox.setOnAction(e -> {
            clientGameSettings.setGameFormat(GameSettings.GameFormat.get(gameFormatChoiceBox.getSelectionModel().getSelectedIndex()));
            try {
                PacketHandler.sendSelfLobby(clientThread, clientGameSettings);
            } catch (IOException e1) { }
        });

        gameModeChoiceBox.setOnAction(e -> {
            clientGameSettings.setGameMode(GameSettings.GameMode.get(gameModeChoiceBox.getSelectionModel().getSelectedIndex()));
            try {
                PacketHandler.sendSelfLobby(clientThread, clientGameSettings);
            } catch (IOException e1) { }
        });

        if(!clientThread.getMember().getUUID().equals(lobbyLeader)){
            gameFormatChoiceBox.setDisable(true);
            gameModeChoiceBox.setDisable(true);
            gameStartBtn.setDisable(true);
            gameEndBtn.setDisable(true);
        }else{
            gameFormatChoiceBox.setDisable(false);
            gameModeChoiceBox.setDisable(false);
            gameStartBtn.setDisable(gameRunning);
            gameEndBtn.setDisable(!gameRunning);
        }

        gameStartBtn.setOnAction(e -> {
            try {
                PacketHandler.sendRequestStartGame(clientThread);
            } catch(IOException e1) { }
        });

        gameEndBtn.setOnAction(e -> {
            try {
                PacketHandler.sendRequestEndGame(clientThread);
            } catch(IOException e1) { }
        });

        ArrayList<BorderPane> panes = new ArrayList<>();
        members.forEach(member -> {
            JSONObject jsonObj = (JSONObject) member;
            BorderPane borderPane = new BorderPane();
            HBox hBox1 = new HBox();
            HBox hBox2 = new HBox();

            if(((String) jsonObj.get("uuid")).equals(clientThread.getMember().getUUID())){
                clientThread.getMember().setFromJSON(jsonObj);
            }

            BorderPane statusPane = new BorderPane();
            ImageView statusImageView = new ImageView();
            statusPane.setCenter(statusImageView);
            statusPane.setPadding(new Insets(4));
            if((Boolean) jsonObj.get("threadRunning")) {
                if((Boolean) jsonObj.get("spectator")){
                    statusImageView.setImage(resourceManager.getSymbols().get("statusSpectator"));
                }else {
                    statusImageView.setImage(resourceManager.getSymbols().get("statusConnected"));
                }
            }else{
                statusImageView.setImage(resourceManager.getSymbols().get("statusDisconnected"));
            }

            BorderPane lobbyLeaderPane = new BorderPane();
            ImageView lobbyLeaderImageView = new ImageView();
            lobbyLeaderImageView.setImage(resourceManager.getSymbols().get("statusLobbyLeader"));
            if(!((String) jsonObj.get("uuid")).equals(lobbyLeader)){
                lobbyLeaderImageView.setVisible(false);
            }
            lobbyLeaderPane.setCenter(lobbyLeaderImageView);
            lobbyLeaderPane.setPadding(new Insets(4));

            BorderPane usernamePane = new BorderPane();
            Label usernameLabel = new Label();
            usernameLabel.setText((String) jsonObj.get("username"));
            usernamePane.setCenter(usernameLabel);
            usernamePane.setPadding(new Insets(4));

            BorderPane spectatorPane = new BorderPane();
            CheckBox spectatorCheckBox = new CheckBox();
            spectatorCheckBox.setSelected((Boolean) jsonObj.get("spectator"));
            spectatorPane.setCenter(spectatorCheckBox);
            spectatorPane.setPadding(new Insets(4));
            spectatorCheckBox.setOnAction(e -> {
                clientThread.getMember().setSpectator(spectatorCheckBox.isSelected());
                try {
                    PacketHandler.sendSelfLobby(clientThread, clientGameSettings);
                } catch (IOException e1) { }
            });

            BorderPane teamPane = new BorderPane();
            ChoiceBox teamChoiceBox = new ChoiceBox();
            teamChoiceBox.getItems().setAll("1", "2", "3", "4");
            teamChoiceBox.getSelectionModel().select(((Long) jsonObj.get("team")).intValue() - 1);
            teamPane.setCenter(teamChoiceBox);
            teamPane.setPadding(new Insets(4));
            teamChoiceBox.setOnAction(e -> {
                clientThread.getMember().setTeam(teamChoiceBox.getSelectionModel().getSelectedIndex() + 1);
                try {
                    PacketHandler.sendSelfLobby(clientThread, clientGameSettings);
                } catch (IOException e1) { }
            });

            if(((Long) gameSettings.get("gameMode")).intValue() == 0){
                teamChoiceBox.setDisable(true);
            }
            if(!clientThread.getMember().getUUID().equals((String) jsonObj.get("uuid"))){
                spectatorCheckBox.setDisable(true);
                teamChoiceBox.setDisable(true);
            }

            hBox1.getChildren().setAll(lobbyLeaderPane, usernamePane);
            hBox2.getChildren().setAll(spectatorPane, teamPane);
            borderPane.setLeft(statusPane);
            borderPane.setCenter(hBox1);
            borderPane.setRight(hBox2);

            panes.add(borderPane);
        });

        connectedListView.getItems().setAll(panes);
    }

    public void updateGame(JSONArray players, boolean gameRunning){
        if(gameRunning){
            if(clientGameManager == null){
                clientGameManager = new ClientGameManager(this, new Game());
                updatePlayers(players);
                clientGameManager.initGame();

                for(Object obj : players) {
                    JSONObject jsonObject = (JSONObject) obj;
                    clientGameManager.getGame().getPlayers().get((String) jsonObject.get("uuid")).setFromJSON(jsonObject, resourceManager);
                    clientGameManager.getPlayerAreas().get(clientGameManager.getGame().getPlayers().get((String) jsonObject.get("uuid"))).syncPanes(resourceManager);
                    clientGameManager.getPlayerAreas().get(clientGameManager.getGame().getPlayers().get((String) jsonObject.get("uuid"))).initAreas(resourceManager);
                }

                if(clientGameManager.getPlayerAreas().containsKey(clientGameManager.getClientPlayer()) && clientGameManager.getClientPlayer().getDeckData() == null){
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(new File("C:\\Users\\NinthWorld\\IdeaProjects\\MagicFX"));
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Supported Formats", "*.dec; *.deck; *.jdeck"));
                    File deckFile = fileChooser.showOpenDialog(primaryStage);
                    //File deckFile = new File("EDH-Boros-Aurelia.jdeck");
                    clientGameManager.getClientPlayer().setDeckData(DeckLoader.loadDeckFile(deckFile, resourceManager.getAllCards()));
                    clientGameManager.getClientPlayer().initGame();
                    clientGameManager.getPlayerAreas().get(clientGameManager.getClientPlayer()).getCommanderPane().getChildren().setAll(CardPane.createCardPane(new CardEntity(clientGameManager.getClientPlayer().getCommander()), resourceManager));
                    clientGameManager.getPlayerAreas().get(clientGameManager.getClientPlayer()).updatePanes(resourceManager);

                    try {
                        PacketHandler.sendUpdatePlayer(clientThread, clientGameManager.getClientPlayer());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(clientGameManager.getClientPlayer() != null) {
                    clientGameManager.getPlayerAreas().get(clientGameManager.getClientPlayer()).updatePaneActions(resourceManager, clientThread);
                }
            }else {
                updatePlayers(players);
            }
        }else{
            clientGameManager = null;
            globalSplitTop.getItems().clear();
            globalSplitBottom.getItems().clear();
        }
    }

    public void updatePlayers(JSONArray players){
        for(Object obj : players){
            JSONObject jsonObject = (JSONObject) obj;
            String UUID = (String) jsonObject.get("uuid");
            if(!clientGameManager.getGame().getPlayers().containsKey(UUID)){
                Player player = new Player();
                player.setTeam(((Long) jsonObject.get("team")).intValue());
                clientGameManager.getGame().getPlayers().put(UUID, player);
            }

            Player player = clientGameManager.getGame().getPlayers().get(UUID);

            if(clientGameManager.getPlayerAreas().containsKey(player)){
                if(!clientGameManager.getGame().getPlayers().containsKey(clientThread.getMember().getUUID()) || !clientGameManager.getGame().getPlayers().get(clientThread.getMember().getUUID()).equals(player)) {
                    player.setFromJSON(jsonObject, resourceManager);
                    clientGameManager.getPlayerAreas().get(player).syncPanes(resourceManager);
                }
            }
        }
    }
}