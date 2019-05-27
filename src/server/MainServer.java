package server;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {
        ServerGame serverGame = new ServerGame();
        try {
            serverGame.init(5568);
            serverGame.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
