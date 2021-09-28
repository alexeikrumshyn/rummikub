package rummikub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/*
    The Player class handles the individual player's logic and actions

    Contains a nested class "Client" which handles an individual player's communication to the server
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    public String name;
    int playerId = 0;
    boolean hasInitialPoints;

    Client clientConnection;
    Game game;
    TileCollection hand;

    public Player(String n) {
        name = n;
        hand = new TileCollection();
        hand.checkIfMeld = false;
        hasInitialPoints = false;
    }

    public static void main(String args[]) {
        Scanner myObj = new Scanner(System.in);
        System.out.print("What is your name ? ");
        String name = myObj.next();
        Player p = new Player(name);
        p.connectToClient();
        p.clientGameLoop();
        myObj.close();
    }

    public void connectToClient() {
        clientConnection = new Client();
    }

    public void connectToClient(int port) {
        clientConnection = new Client(port);
    }

    /* Draws the player's initial tiles */
    public void drawInitialTiles() {
        for (int i = 0; i < Config.INITIAL_HAND_SIZE; ++i) {
            drawTile();
        }
    }

    /* Draws a random tile from the stock and adds it to Player's hand */
    public void drawTile() {
        Tile drawnTile = game.removeFromStock();
        hand.add(drawnTile);
    }

    /* Draws tile given by str from the stock and adds it to Player's hand */
    public void drawTile(String str) {
        Tile drawnTile = game.removeFromStock(str);
        hand.add(drawnTile);
    }

    /* Handles playing meld given by str on table, and returns the TileCollection object */
    public TileCollection playMeld(String str) {

        //separate tiles used in meld from hand and from table
        String[] tiles = str.split("\\s+");

        ArrayList<Tile> fromHand = new ArrayList<>();
        ArrayList<String> fromTable = new ArrayList<>();

        for (String tile_str : tiles) {
            Tile removed = hand.remove(tile_str);
            if (removed == null) { //i.e. not found in hand
                fromTable.add(tile_str);
            } else {
                fromHand.add(removed);
            }
        }
        return game.createMeld(fromHand, fromTable);
    }

    /* Returns the player's hand */
    public String getHand() {
        return hand.toString();
    }

    /* Returns the player's view of the table and their hand */
    public String getGameState() {
        String str = "==========TABLE==========" + "\n";
        str += game.getTable() + "\n";

        str += "==========HAND==========" + "\n";
        str += getHand() + "\n\n";

        return str;
    }

    /* Returns true if initial point threshold has been surpassed */
    public boolean hasInitialPoints() {
        return hasInitialPoints;
    }

    /* Client-side game loop */
    public void clientGameLoop() {
        int counter = 0;
        while (true) {
            updateGame();
            if (counter == 0)
                drawInitialTiles();
            if (game.isOver())
                break;
            getAction();

            if (hand.toString().equals(""))
                game.setOver();

            sendUpdatedGame();
            ++counter;
        }
    }

    public void updateGame() {
        game = clientConnection.receiveGameState();
    }

    public void sendUpdatedGame() {
        clientConnection.sendGameState(game);
    }

    /* Prompts the user for an action for their turn */
    public void getAction() {
        Scanner scn = new Scanner(System.in).useDelimiter("\n");
        String action = "";
        ArrayList<TileCollection> meldsPlayed = new ArrayList<>();

        while (true) {
            if (action.equals("end_turn"))
                break;

            System.out.println(getGameState());
            System.out.println("Select an action: ");
            System.out.println("(1) Play Meld on Table");
            System.out.println("(2) Draw Tile and End Turn");
            System.out.println("(3) End Turn");
            String choice = scn.next();

            switch (choice) {
                case "1":
                    System.out.println("Type Meld as space-separated tiles (eg. R5 B5 G5): ");
                    String meldStr = scn.next();
                    TileCollection played = playMeld(meldStr);
                    meldsPlayed.add(played);
                    break;
                case "2":
                    drawTile();
                    action = "end_turn";
                    break;
                case "3":
                    action = "end_turn";
                    break;
            }
        }
        if (action.equals("end_turn")) {
            if (!hasInitialPoints)
                checkInitialPoints(meldsPlayed);

            if (hand.toString().equals("")) {
                game.setWinner(name);
            }
        }
    }

    /* Checks if player just put down initial threshold of points */
    public void checkInitialPoints(ArrayList<TileCollection> meldsPlayed) {
        int pts = 0;
        for (TileCollection c : meldsPlayed) {
            pts += c.getPoints();
        }
        if (pts >= Config.INITIAL_POINTS_THRESHOLD) {
            hasInitialPoints = true;
        }
    }


    /*
        The Client class handles individual player's client activities
     */
    private class Client {
        Socket socket;
        private ObjectInputStream dIn;
        private ObjectOutputStream dOut;

        public Client() {
            this(Config.GAME_SERVER_PORT_NUMBER);
        }

        public Client(int portId) {
            try {
                socket = new Socket("localhost", portId);
                dOut = new ObjectOutputStream(socket.getOutputStream());
                dIn = new ObjectInputStream(socket.getInputStream());

                playerId = dIn.readInt();
                System.out.println("Connected as " + playerId);

            } catch (IOException ex) {
                System.out.println("Client failed to open");
            }
        }

        public Game receiveGameState() {
            try {
                return (Game) dIn.readObject();
            } catch (IOException e) {
                System.out.println("Game data not received");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                e.printStackTrace();
            }
            return null;
        }

        public void sendGameState(Game game) {
            try {
                dOut.writeObject(game);
                dOut.flush();
            } catch (Exception e) {
                System.out.println("Game state not sent");
                e.printStackTrace();
            }
        }

    }

}
