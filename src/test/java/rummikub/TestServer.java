package rummikub;

import java.util.concurrent.TimeUnit;

public class TestServer {
    Thread thread;
    GameServer gameServer;

    public TestServer(int numRounds) throws InterruptedException {
        gameServer = new GameServer();

        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    gameServer.acceptConnections();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                gameServer.gameLoop(numRounds);
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(1);
    }
}
