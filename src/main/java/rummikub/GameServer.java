package rummikub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/*
    The GameServer class handles the server portion of the game

    Contains a nested class "Server" which handles an individual player's server activities
 */
public class GameServer implements Serializable {

    private static final long serialVersionUID = 1L;

    ServerSocket ss;
    Server[] playerServer = new Server[3];
    Player[] players = new Player[3];

    int numPlayers;
    Game game;

    public GameServer() {
        System.out.println("Starting game server");
        numPlayers = 0;
        game = new Game();

        // initialize the players list with new players
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(" ");
        }

        try {
            ss = new ServerSocket(Config.GAME_SERVER_PORT_NUMBER);
        } catch (IOException ex) {
            System.out.println("Server Failed to open");
        }
    }

    public static void main(String args[]) throws Exception {
        GameServer sr = new GameServer();

        sr.acceptConnections();
        sr.gameLoop(Integer.MAX_VALUE);
    }

    public void acceptConnections() throws ClassNotFoundException {
        try {
            System.out.println("Waiting for players...");
            while (numPlayers < Config.MAX_PLAYERS) {
                Socket s = ss.accept();
                numPlayers++;

                Server server = new Server(s, numPlayers);

                // send the player number
                server.dOut.writeInt(server.playerId);
                server.dOut.flush();

                playerServer[numPlayers - 1] = server;
            }
            System.out.println("Three players have joined the game");

            // start the server threads
            for (int i = 0; i < playerServer.length; i++) {
                Thread t = new Thread(playerServer[i]);
                t.start();
            }
            // start their threads
        } catch (IOException ex) {
            System.out.println("Could not connect 3 players");
        }
    }

    public void kill() throws IOException {
        ss.close();
    }

    public void gameLoop(int numRounds) {
        try {
            int counter = 1;
            while (!game.isOver()) {
                for (int i = 0; i < numPlayers; ++i) {
                    if (counter > numRounds)
                        return;
                    System.out.println("PLAYER " + (i+1) + "'s turn");
                    playerServer[i].sendGameState();
                    playerServer[i].receiveAndUpdateGameState();
                    if (game.isOver()) {
                        //ensure the remaining players can get final scores
                        int j = (i+1)%Config.MAX_PLAYERS;
                        while (j != i) {
                            playerServer[j].sendGameState();
                            playerServer[j].receiveAndUpdateGameState();
                            j = (j+1)%Config.MAX_PLAYERS;
                        }
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


    /*
        The Server class handles individual player's server activities
     */
    public class Server implements Runnable {

        private Socket socket;
        private ObjectInputStream dIn;
        private ObjectOutputStream dOut;
        private int playerId;

        public Server(Socket s, int playerid) throws SocketException {
            socket = s;
            playerId = playerid;
            try {
                dOut = new ObjectOutputStream(socket.getOutputStream());
                dIn = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                System.out.println("Server Connection failed");
            }
        }

        /*
         * run function for threads --> main body of the thread will start here
         */
        public void run() {
            try {
                while (true) {}
            } catch (Exception ex) {
                {
                    System.out.println("Run failed");
                    ex.printStackTrace();
                }
            }
        }

        public void sendGameState() {
            try {
                dOut.writeObject(game);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Game data not received");
                e.printStackTrace();
            }
        }

        public void receiveAndUpdateGameState() {
            try {
                game = (Game) dIn.readObject();
            } catch (IOException e) {
                System.out.println("Game data not received");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                e.printStackTrace();
            }
        }

        public void sendTurnNo(int r) {
            try {
                dOut.writeInt(r);
                dOut.flush();
            } catch (Exception e) {
                System.out.println("Turn not sent");
                e.printStackTrace();
            }
        }
    }

}
