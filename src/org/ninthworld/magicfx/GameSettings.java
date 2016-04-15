package org.ninthworld.magicfx;

import org.json.simple.JSONObject;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class GameSettings {
    public enum GameFormat {
        STANDARD (0), COMMANDER(1);

        private final int index;
        GameFormat(int index){
            this.index = index;
        }

        public static GameFormat get(int i){
            switch (i){
                case 1:
                    return COMMANDER;
                default:
                    return STANDARD;
            }
        }

        public int getIndex(){
            return index;
        }
    }

    public enum GameMode {
        FFA (0), TEAMS (1);

        private final int index;
        GameMode(int index){
            this.index = index;
        }

        public static GameMode get(int i){
            switch (i){
                case 1:
                    return TEAMS;
                default:
                    return FFA;
            }
        }

        public int getIndex(){
            return index;
        }
    }

    private GameFormat gameFormat;
    private GameMode gameMode;

    public GameSettings(){
        this.gameFormat = GameFormat.COMMANDER;
        this.gameMode = GameMode.FFA;
    }

    public GameFormat getGameFormat() {
        return gameFormat;
    }

    public void setGameFormat(GameFormat gameFormat) {
        this.gameFormat = gameFormat;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("gameFormat", gameFormat.getIndex());
        jsonObj.put("gameMode", gameMode.getIndex());

        return jsonObj;
    }

    public void setFromJSON(JSONObject jsonObject){
        gameFormat = GameFormat.get(((Long) jsonObject.get("gameFormat")).intValue());
        gameMode = GameMode.get(((Long) jsonObject.get("gameMode")).intValue());
    }
}
