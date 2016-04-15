package org.ninthworld.magicfx;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class Packet {

    private String packetName;
    private Map<String, Object> mapData;

    public Packet(String packetName){
        this.packetName = packetName;
        this.mapData = new HashMap<>();
        this.mapData.put("packetName", this.packetName);
    }

    public Packet addData(String dataName, Object data){
        mapData.put(dataName, data);
        return this;
    }

    public Map<String, Object> getMapData(){
        return mapData;
    }

    public void setMapData(Map<String, Object> map){
        this.mapData = map;
    }

    public String toString(){
        JSONObject mainObj = new JSONObject(mapData);
        return mainObj.toJSONString() + "\n";
    }

    public static Packet readPacket(String packetData) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject mainObj = (JSONObject) parser.parse(packetData);
        Packet packet = new Packet("");
        packet.setMapData(mainObj);
        return packet;
    }
}
