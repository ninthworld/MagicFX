package org.ninthworld.magicfx;

import java.util.HashMap;

/**
 * Created by NinthWorld on 4/15/2016.
 */
public class Game {

    private HashMap<String, Player> players;

    public Game(){
        this.players = new HashMap<>();
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }
}
