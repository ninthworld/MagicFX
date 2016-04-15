package org.ninthworld.magicfx.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class ConnectionListener extends Thread {

    private boolean threadRunning;
    private ServerSocket serverSocket;
    private ServerManager serverManager;

    public ConnectionListener(ServerSocket serverSocket, ServerManager serverManager){
        this.threadRunning = true;
        this.serverSocket = serverSocket;
        this.serverManager = serverManager;
    }

    @Override
    public void run(){
        try {
            while(threadRunning) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(100);
                ServerThread module = new ServerThread(socket, serverManager);
                System.out.printf("Client connected.\n");
                module.start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
