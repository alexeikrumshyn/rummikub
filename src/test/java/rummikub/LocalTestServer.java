package rummikub;

public class LocalTestServer {

    Game game;
    int numPlayers;
    Player[] players;

    public LocalTestServer() {
        System.out.println("Starting game server");
        game = new Game();
        numPlayers = Config.MAX_PLAYERS;
        players = new Player[Config.MAX_PLAYERS];

        // initialize the players list with new players
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("p"+(i+1));
            players[i].playerId = i+1;
            players[i].game = this.game;
        }

    }

    public void gameLoop(int numRounds) {
        try {
            int counter = 1;
            while (!game.isOver()) {
                for (int i = 0; i < numPlayers; ++i) {
                    if (counter > numRounds)
                        return;
                    System.out.println("PLAYER " + (i+1) + "'s turn");
                    players[i].game = this.game;
                    players[i].getAction();
                    this.game = players[i].game;
                    if (game.isOver()) {
                        break;
                    }
                    counter++;
                }
            }
            if (numRounds != -1) {
                return;
            }
            System.out.println("Game is over and the winner is: " + getWinner());
            int[] finalScores = game.getScores();
            System.out.println("Final Scores: " + "\nPlayer 1: " + finalScores[0]*-1 + "\nPlayer 2: " + finalScores[1]*-1 + "\nPlayer 3: " + finalScores[2]*-1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getWinner() {
        return game.getWinner();
    }

    public static void main(String args[]) throws Exception {
        LocalTestServer localTestServer = new LocalTestServer();
        for (int i = 0; i < localTestServer.players.length; i++) {
            localTestServer.players[i].drawInitialTiles();
        }
        localTestServer.gameLoop(Integer.MAX_VALUE);
    }
}
