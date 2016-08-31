package org.ninthworld.magicfx.client;

import javafx.stage.FileChooser;
import org.ninthworld.magicfx.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NinthWorld on 4/15/2016.
 */
public class ClientGameManager {

    private ClientMain parent;
    private Game game;

    private HashMap<Player, PlayerArea> playerAreas;

    public ClientGameManager(ClientMain parent, Game game){
        this.parent = parent;
        this.game = game;
        this.playerAreas = new HashMap<>();
    }

    public void initGame(){
        boolean isTeam = parent.getClientGameSettings().getGameMode().equals(GameSettings.GameMode.TEAMS);
        boolean containsClient = game.getPlayers().containsKey(parent.getClientThread().getMember().getUUID());

        if(containsClient){
            PlayerArea clientArea = PlayerArea.createBottomPlayerArea(parent.getResourceManager(), getClientPlayer());
            playerAreas.put(clientArea.getPlayer(), clientArea);

            for(Map.Entry<String, Player> entry : game.getPlayers().entrySet()){
                if(!playerAreas.containsKey(entry.getValue())) {
                    PlayerArea playerArea;
                    if (isTeam && entry.getValue().getTeam() == getClientPlayer().getTeam()) {
                        playerArea = PlayerArea.createBottomPlayerArea(parent.getResourceManager(), entry.getValue());
                    } else {
                        playerArea = PlayerArea.createTopPlayerArea(parent.getResourceManager(), entry.getValue());
                    }
                    playerAreas.put(entry.getValue(), playerArea);
                }
            }

        }else{
            int bottomTeam = 0;
            for(Map.Entry<String, Player> entry : game.getPlayers().entrySet()){
                if(bottomTeam == 0){
                    bottomTeam = entry.getValue().getTeam();
                }

                PlayerArea playerArea;
                if(entry.getValue().getTeam() == bottomTeam){
                    playerArea = PlayerArea.createBottomPlayerArea(parent.getResourceManager(), entry.getValue());
                }else{
                    playerArea = PlayerArea.createTopPlayerArea(parent.getResourceManager(), entry.getValue());
                }
                playerAreas.put(entry.getValue(), playerArea);
            }
        }

        for(Map.Entry<Player, PlayerArea> playerArea : playerAreas.entrySet()){
            if(playerArea.getValue().isTopArea()){
                parent.getGlobalSplitTop().getItems().add(playerArea.getValue().getParentGridPane());
            }else{
                parent.getGlobalSplitBottom().getItems().add(playerArea.getValue().getParentGridPane());
            }

            playerArea.getValue().initAreas(parent.getResourceManager());
            parent.getResourceManager().getScene().widthProperty().addListener(e -> {
                playerArea.getValue().updatePanes(parent.getResourceManager());
            });
            parent.getResourceManager().getScene().heightProperty().addListener(e -> {
                playerArea.getValue().updatePanes(parent.getResourceManager());
            });
            //playerArea.getValue().syncPanes(parent.getResourceManager());

            if(getClientPlayer() != null) {
                if (getClientPlayer().equals(playerArea.getKey())) {
                    playerArea.getValue().updatePaneActions(parent.getResourceManager(), parent.getClientThread());
                }
            }
        }
    }

    public HashMap<Player, PlayerArea> getPlayerAreas(){
        return playerAreas;
    }

    public Player getClientPlayer(){
        return game.getPlayers().get(parent.getClientThread().getMember().getUUID());
    }

    public Game getGame() {
        return game;
    }
}
