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

    public static void sendUpdatePlayer(ClientThread thread, Player player) throws IOException {
        Packet packet = new Packet("updatePlayer");
        packet.addData("player", player.toJSONObject()).addData("uuid", thread.getMember().getUUID());
        thread.getOutputStream().writeBytes(packet.toString());
    }

    public static void sendRequestGameInfo(ClientThread thread) throws IOException {
        Packet packet = new Packet("requestGameInfo");
        thread.getOutputStream().writeBytes(packet.toString());
    }

    public static void sendRequestStartGame(ClientThread thread) throws IOException {
        Packet packet = new Packet("requestStartGame");
        thread.getOutputStream().writeBytes(packet.toString());
    }

    public static void sendRequestEndGame(ClientThread thread) throws IOException {
        Packet packet = new Packet("requestEndGame");
        thread.getOutputStream().writeBytes(packet.toString());
    }

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
        sendPacket.addData("lobbyLeader", thread.getServerManager().getLobbyLeaderUUID()).addData("members", jsonArray).addData("gameRunning", thread.getServerManager().getGame() != null);
        sendPacket.addData("gameSettings", thread.getServerManager().getGameSettings().toJSONObject());

        for(ServerThread serverThread : thread.getServerManager().getConnections().values()){
            serverThread.addSendPacket(sendPacket);
        }
    }

    public static void sendGameInfo(ServerThread thread) throws IOException {
        Packet packet = new Packet("gameInfo");

        Game game = thread.getServerManager().getGame();
        JSONArray players = new JSONArray();
        if(game != null){
            for(Map.Entry<String, Player> entry : game.getPlayers().entrySet()){
                JSONObject playerObj = entry.getValue().toJSONObject();
                playerObj.put("uuid", entry.getKey());
                players.add(playerObj);
            }
        }
        packet.addData("gameRunning", game != null).addData("players", players);

        thread.addSendPacket(packet);
    }

    private static void handleClient(ClientThread thread, Packet packet) throws IOException {
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
            Platform.runLater(() -> thread.getParent().updateLobby(members, gameSettings, (String) packetData.get("lobbyLeader"), (Boolean) packetData.get("gameRunning")));
        }else if(packetName.equals("gameInfo")){
            boolean gameRunning = (Boolean) packetData.get("gameRunning");
            JSONArray players = (JSONArray) packetData.get("players");

            sendRequestLobbyData(thread);
            Platform.runLater(() -> thread.getParent().updateGame(players, gameRunning));
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
        }else if(packetName.equals("requestStartGame")){
            if(thread.getServerManager().getGame() == null){
                thread.getServerManager().createGame();
                for(ServerThread serverThread : thread.getServerManager().getConnections().values()){
                    sendGameInfo(serverThread);
                }
            }
        }else if(packetName.equals("requestEndGame")){
            if(thread.getServerManager().getGame() != null){
                thread.getServerManager().endGame();
                for(ServerThread serverThread : thread.getServerManager().getConnections().values()){
                    sendGameInfo(serverThread);
                }
            }
        }else if(packetName.equals("requestGameInfo")){
            sendGameInfo(thread);
        }else if(packetName.equals("updatePlayer")){
            if(thread.getServerManager().getGame() != null){
                if(thread.getServerManager().getGame().getPlayers().containsKey((String) packetData.get("uuid"))){
                    thread.getServerManager().getGame().getPlayers().get((String) packetData.get("uuid")).setFromJSON((JSONObject) packetData.get("player"), null);
                    for(ServerThread serverThread : thread.getServerManager().getConnections().values()){
                        sendGameInfo(serverThread);
                    }
                }
            }
        }
    }

    public static void handleInput(Thread thread, String inMsg) throws IOException {
        Packet packet = new Packet("null");
        try {
            packet = Packet.readPacket(inMsg);
        } catch(ParseException e){
            System.out.println("Error: Packet corrupted.");
        }
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
