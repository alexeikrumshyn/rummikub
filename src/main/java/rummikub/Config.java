package rummikub;

import java.util.concurrent.TimeUnit;

public class Config {
    public static final int GAME_SERVER_PORT_NUMBER = 3010;
    public static final int MAX_PLAYERS = 3;
    public static final int INITIAL_HAND_SIZE = 14;

    public static GameServer startTestServer() throws InterruptedException {

        GameServer gameServer = new GameServer();

        Thread srv = new Thread(new Runnable() {
            public void run() {
                try {
                    gameServer.acceptConnections();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                gameServer.gameLoop();
            }
        });
        srv.start();
        TimeUnit.SECONDS.sleep(1);
        return gameServer;
    }


}
