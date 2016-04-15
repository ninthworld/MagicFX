package org.ninthworld.magicfx.server;

import org.ninthworld.magicfx.GameSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class ServerManager {

    private HashMap<String, ServerThread> connections;
    private String lobbyLeaderUUID;
    private GameSettings gameSettings;

    public ServerManager(){
        this.gameSettings = new GameSettings();
        this.lobbyLeaderUUID = null;
        this.connections = new HashMap<>();
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
}
