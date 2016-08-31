package org.ninthworld.magicfx.server;

import org.ninthworld.magicfx.Game;
import org.ninthworld.magicfx.GameSettings;
import org.ninthworld.magicfx.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class ServerManager {

    private HashMap<String, ServerThread> connections;
    private String lobbyLeaderUUID;
    private GameSettings gameSettings;
    private Game game;

    public ServerManager(){
        this.gameSettings = new GameSettings();
        this.lobbyLeaderUUID = null;
        this.connections = new HashMap<>();
        this.game = null;
    }

    public void createGame(){
        game = new Game();
        for(Map.Entry<String, ServerThread> entry : connections.entrySet()) {
            if (entry.getValue().isRunning() && !entry.getValue().getMember().isSpectator()) {
                Player player = new Player();
                player.setName(entry.getValue().getMember().getUsername());
                player.setTeam(entry.getValue().getMember().getTeam());
                player.setLife((gameSettings.getGameFormat() == GameSettings.GameFormat.COMMANDER ? 40 : 20));
                game.getPlayers().put(entry.getKey(), player);
            }
        }
    }

    public void endGame(){
        game = null;
    }

    public HashMap<String, ServerThread> getConnections(){
        return connections;
    }

    public void putConnection(String UUID, ServerThread thread) {
        connections.put(UUID, thread);
        updateLobbyLeader();
    }

    public void updateLobbyLeader(){
        if(lobbyLeaderUUID == null || !connections.get(lobbyLeaderUUID).isRunning()){
            String UUID = null;
            for(Map.Entry<String, ServerThread> entry : connections.entrySet()){
                if(entry.getValue().isRunning()){
                    UUID = entry.getKey();
                    break;
                }
            }

            lobbyLeaderUUID = UUID;
        }
    }

    public String getLobbyLeaderUUID(){
        return lobbyLeaderUUID;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
