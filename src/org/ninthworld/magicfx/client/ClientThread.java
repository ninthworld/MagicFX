package org.ninthworld.magicfx.client;

import javafx.application.Application;
import org.json.simple.parser.ParseException;
import org.ninthworld.magicfx.Member;
import org.ninthworld.magicfx.Packet;
import org.ninthworld.magicfx.PacketHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class ClientThread extends Thread {

    private boolean isRunning;
    private ClientMain parent;
    private String ip;
    private int port;
    private Socket socket;
    private Member member;

    public ClientThread(String ip, int port, String username, String UUID, ClientMain parent) throws IOException {
        this.isRunning = true;
        this.parent = parent;
        this.ip = ip;
        this.port = port;
        this.member = new Member(UUID, username);
        this.socket = new Socket(this.ip, this.port);
        this.socket.setSoTimeout(100);
    }

    @Override
    public void run(){
        try {
            BufferedReader in = getInputStream();
            DataOutputStream out = getOutputStream();
            PacketHandler.sendHandshake(this, member.getUUID(), member.getUsername());
            PacketHandler.sendRequestLobbyData(this);

            while(isRunning) {
                try {
                    String inMsg = in.readLine();
                    // System.out.println("> Server: " + inMsg);

                    PacketHandler.handleInput(this, inMsg);
                } catch (SocketTimeoutException e){ }
            }
        } catch(IOException | ParseException e){
            e.printStackTrace();
        }
    }

    public DataOutputStream getOutputStream() throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }

    public BufferedReader getInputStream() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Member getMember(){
        return member;
    }

    public ClientMain getParent(){
        return parent;
    }

    public static ClientThread createConnection(ClientMain parent, Application.Parameters params) throws IOException {
        Map<String, String> namedParams = params.getNamed();
        String[] serverParts = namedParams.get("server").split(":");
        int port = 25565;
        if(serverParts.length > 1){
            port = Integer.parseInt(serverParts[1]);
        }
        return new ClientThread(serverParts[0], port, namedParams.get("user"), namedParams.get("pass"), parent);
    }

    public void setRunning(boolean run){
        this.isRunning = run;
    }
}
