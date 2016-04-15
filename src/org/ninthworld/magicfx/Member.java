package org.ninthworld.magicfx;

import org.json.simple.JSONObject;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class Member {

    private final String UUID;
    private String username;
    private boolean spectator;
    private int team;

    public Member(String UUID, String username){
        this.UUID = UUID;
        this.username = username;
        this.spectator = false;
        this.team = 1;
    }

    public String getUUID() {
        return UUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", UUID);
        jsonObject.put("username", username);
        jsonObject.put("spectator", spectator);
        jsonObject.put("team", team);

        return jsonObject;
    }

    public void setFromJSON(JSONObject memberObj){
        username = (String) memberObj.get("username");
        spectator = (Boolean) memberObj.get("spectator");
        team = ((Long) memberObj.get("team")).intValue();
    }
}
