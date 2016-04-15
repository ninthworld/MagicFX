package org.ninthworld.magicfx.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class ServerMain implements Runnable {

    private int port;
    private ServerSocket serverSocket;
    private ConnectionListener connListener;
    private ServerManager serverManager;

    public ServerMain() throws IOException {
        this.serverManager = new ServerManager();
        this.port = 25565;
        this.serverSocket = new ServerSocket(port);
        this.connListener = new ConnectionListener(serverSocket, serverManager);
    }

    public void run(){
        System.out.printf("Listening for connections on port %d...\n", port);
        connListener.start();
    }

    public static void main(String[] args) throws IOException {
        new Thread(new ServerMain()).start();
    }
}
