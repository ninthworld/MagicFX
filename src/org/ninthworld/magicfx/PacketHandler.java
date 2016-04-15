package org.ninthworld.magicfx;

import javafx.application.Platform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.ninthworld.magicfx.client.ClientThread;
import org.ninthworld.magicfx.server.ServerThread;

import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class PacketHandler {

    public static void sendSelfLobby(ClientThread thread, GameSettings gameSettings) throws IOException {
        Packet packet = new Packet("selfLobby");
        packet.addData("member", thread.getMember().toJSONObject());
        packet.addData("gameSettings", gameSettings.toJSONObject());
        thread.getOutputStream().writeBytes(packet.toString());
    }

    public static void sendHandshake(ClientThread thread, String UUID, String username) throws IOException {
        Packet packet = new Packet("handshake");
        packet.addData("username", username).addData("uuid", UUID);
        thread.getOutputStream().writeBytes(packet.toString());
    }

    public static void sendChatMessage(ClientThread thread, String message) throws IOException {
        Packet packet = new Packet("chatMessage");
        packet.addData("username", thread.getMember().getUsername()).addData("uuid", thread.getMember().getUUID()).addData("message", message);
        thread.getOutputStream().writeBytes(packet.toString());
    }

    public static void sendRequestLobbyData(ClientThread thread) throws IOException {
        Packet packet = new Packet("requestLobbyData");
        thread.getOutputStream().writeBytes(packet.toString());
    }

    public static void sendLobbyData(ServerThread thread) throws IOException {
        Packet sendPacket = new Packet("lobbyData");
        JSONArray jsonArray = new JSONArray();
        for(ServerThread serverThread : thread.getServerManager().getConnections().values()){
            JSONObject jsonObject = serverThread.getMember().toJSONObject();
            jsonObject.put("threadRunning", serverThread.isRunning());
            jsonArray.add(jsonObject);
        }
        sendPacket.addData("lobbyLeader", thread.getServerManager().getLobbyLeaderUUID()).addData("members", jsonArray);
        sendPacket.addData("gameSettings", thread.getServerManager().getGameSettings().toJSONObject());

        for(ServerThread serverThread : thread.getServerManager().getConnections().values()){
            serverThread.addSendPacket(sendPacket);
        }
    }

    private static void handleClient(ClientThread thread, Packet packet){
        Map<String, Object> packetData = packet.getMapData();
        String packetName = (String) packetData.get("packetName");

        if(packetName.equals("chatMessage")){
            String UUID = (String) packetData.get("uuid");
            String username = (String) packetData.get("username");
            String message = (String) packetData.get("message");
            Platform.runLater(() -> thread.getParent().addChatMessage(username, message));
        }else if(packetName.equals("lobbyData")){
            JSONArray members = (JSONArray) packetData.get("members");
            JSONObject gameSettings = (JSONObject) packetData.get("gameSettings");
            Platform.runLater(() -> thread.getParent().updateLobby(members, gameSettings, (String) packetData.get("lobbyLeader")));
        }
    }

    private static void handleServer(ServerThread thread, Packet packet) throws IOException {
        Map<String, Object> packetData = packet.getMapData();
        String packetName = (String) packetData.get("packetName");

        if(packetName.equals("chatMessage")){
            for(ServerThread serverThread : thread.getServerManager().getConnections().values()){
                serverThread.addSendPacket(packet);
            }
        }else if(packetName.equals("requestLobbyData")){
            sendLobbyData(thread);
        }else if(packetName.equals("selfLobby")){
            JSONObject memberObj = (JSONObject) packetData.get("member");
            JSONObject gameSettingsObj = (JSONObject) packetData.get("gameSettings");
            String UUID = (String) memberObj.get("uuid");
            thread.getServerManager().getConnections().get(UUID).getMember().setFromJSON(memberObj);
            if(thread.getServerManager().getLobbyLeaderUUID().equals(UUID)) {
                thread.getServerManager().getGameSettings().setFromJSON(gameSettingsObj);
            }
            sendLobbyData(thread);
        }
    }

    public static void handleInput(Thread thread, String inMsg) throws ParseException, IOException {
        Packet packet = Packet.readPacket(inMsg);
        Map<String, Object> packetData = packet.getMapData();

        if(packetData.containsKey("packetName")){
            if(thread instanceof ClientThread){
                handleClient((ClientThread) thread, packet);
            }else if(thread instanceof ServerThread){
                ServerThread serverThread = (ServerThread) thread;
                if(serverThread.getMember() != null) {
                    handleServer(serverThread, packet);
                }else{
                    String packetName = (String) packetData.get("packetName");

                    if(packetName.equals("handshake")){
                        serverThread.setMember(new Member((String) packetData.get("uuid"), (String) packetData.get("username")));
                        serverThread.getServerManager().putConnection(serverThread.getMember().getUUID(), serverThread);

                        System.out.println("Handshake from " + serverThread.getMember().getUsername() + " [" + serverThread.getMember().getUUID() + "]");
                    }
                }
            }
        }
    }
}
