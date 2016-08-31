package org.ninthworld.magicfx.server;

import org.json.simple.parser.ParseException;
import org.ninthworld.magicfx.Member;
import org.ninthworld.magicfx.Packet;
import org.ninthworld.magicfx.PacketHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class ServerThread extends Thread {

    private boolean isRunning;
    private Socket socket;
    private ServerManager serverManager;

    private ArrayList<Packet> packetsToSend;
    private Member member;

    public ServerThread(Socket socket, ServerManager serverManager){
        this.isRunning = true;
        this.socket = socket;
        this.serverManager = serverManager;

        this.packetsToSend = new ArrayList<>();
        this.member = null;
    }

    public void addSendPacket(Packet packet){
        packetsToSend.add(packet);
    }

    @Override
    public void run(){
        try {
            BufferedReader in = getInputStream();
            DataOutputStream out = getOutputStream();

            while(isRunning){
                try {
                    String inMsg = in.readLine();
                    // System.out.println("Client message: " + inMsg);

                    PacketHandler.handleInput(this, inMsg);
                } catch (SocketTimeoutException e){ }

                if(packetsToSend.size() > 0){
                    ArrayList<Packet> tempList = new ArrayList<>();
                    tempList.addAll(packetsToSend);
                    for(Packet packet : tempList){
                        out.writeBytes(packet.toString());
                        packetsToSend.remove(packet);
                    }
                }
            }
        } catch(SocketException e){
            System.out.println("Client disconnected.");
        } catch(IOException e){
            e.printStackTrace();
        }

        isRunning = false;

        try {
            serverManager.updateLobbyLeader();
            PacketHandler.sendLobbyData(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataOutputStream getOutputStream() throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }

    public BufferedReader getInputStream() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public ServerManager getServerManager(){
        return serverManager;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setRunning(boolean run){
        this.isRunning = run;
    }

    public boolean isRunning(){
        return isRunning;
    }
}
