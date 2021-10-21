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
    private boolean mustDrawTile;
    int score;

    Client clientConnection;
    Game game;
    TileCollection hand;

    public Player(String n) {
        name = n;
        hand = new TileCollection();
        hand.checkIfMeld = false;
        hasInitialPoints = false;
        mustDrawTile = true;
        score = 0;
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

    /* Tests whether given tiles are a valid meld */
    public boolean isValidMeld(String str) {
        //remove table reuse indicators
        str = str.replaceAll("\\d:", "");
        TileCollection testCollection = new TileCollection(str);
        return testCollection.isMeld();
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

            sendUpdatedGame();
            ++counter;
        }
    }

    public void updateGame() {
        game = clientConnection.receiveGameState();
        if (game.isOver()) {
            game.setScore(playerId, hand.getPoints());
            sendUpdatedGame();
        }
    }

    public void sendUpdatedGame() {
        game.setScore(playerId, hand.getPoints());
        clientConnection.sendGameState(game);
    }

    /* Returns the list of options for player to take */
    public String getOptions() {
        String opts = "";
        opts += "Select an action: \n" +
                "(1) Play Meld on Table\n" +
                "(2) Draw Tile and End Turn\n";

        if (!mustDrawTile)
            opts += "(3) End Turn\n";

        return opts;
    }

    /* Checks if player typed valid tiles */
    public boolean allTilesExist(String play) {
        String[] playedTiles = play.split("\\s+");
        for (String tileStr : playedTiles) {
            //table reuse case
            if (tileStr.contains(":")) {
                String[] splitStr = tileStr.split(":");
                if (!game.tableContains(Integer.parseInt(splitStr[0])-1, splitStr[1]))
                    return false;
            }
            //from hand case
            else {
                if (!hand.contains(tileStr))
                    return false;
            }
        }
        return true;
    }

    /* Logic for invalid move */
    public void handleInvalidMove(Game beforeTurn, TileCollection handBeforeTurn, boolean penalty) {
        game = new Game(beforeTurn); //reset everything
        hand = new TileCollection(handBeforeTurn);
        if (!penalty)
            return;
        for (int i = 0; i < Config.PENALTY_TILES; ++i)
            drawTile();
    }

    /* Checks table for invalid melds and available jokers, and returns number of freed jokers */
    public int checkTable(Game beforeTurn, TileCollection handBeforeTurn, boolean endOfTurn) {
        ArrayList<Tile> invalidTiles = game.checkTable(endOfTurn);
        if (invalidTiles.size() == 0)
            return 0;
        if (endOfTurn) {
            handleInvalidMove(beforeTurn, handBeforeTurn, true);
            System.out.println("Reuse of table has resulted in invalid melds. Three tiles have been drawn as penalty.");
            return 0;
        }

        int jokerCounter = 0;
        for (Tile t : invalidTiles) {
            if (t.getNumber().equals("*") && t.getColour().equals("*"))
                jokerCounter++;
        }
        return jokerCounter;
    }

    /* Checks if reused joker has only been used with tiles from hand */
    public boolean validJokerReuse(String play) {
        if (!play.contains(":*"))
            return true;

        String[] playedTiles = play.split("\\s+");

        int meldAffected = -1;
        for (String tileStr : playedTiles) {
            //invalid play if reused tiles other than joker in different melds
            //valid when just adding to a run without replacing joker
            //this occurs when all tiles reused from the same meld
            if (tileStr.contains(":")) {
                String[] splitStr = tileStr.split(":");
                int thisMeld = Integer.parseInt(splitStr[0]);
                if (meldAffected == -1) {
                    meldAffected = thisMeld;
                }
                if (thisMeld != meldAffected) {
                    return false;
                }
            }
        }
        return true;
    }

    /* Prompts the user for an action for their turn */
    public void getAction() {
        game.resetTableTileSources();
        Scanner scn = new Scanner(System.in).useDelimiter("\n");
        String action = "";
        mustDrawTile = true;
        ArrayList<TileCollection> meldsPlayed = new ArrayList<>();
        Game beforeTurn = new Game(game);
        TileCollection handBeforeTurn = new TileCollection(hand);

        while (true) {
            if (action.equals("end_turn"))
                break;

            System.out.println(getGameState());
            int jokersAvailable = checkTable(beforeTurn, handBeforeTurn, false);
            if (jokersAvailable > 0)
                System.out.println("You have "+jokersAvailable+" additional joker(s) available on the table to use in a meld with your own tiles.");
            System.out.println(getOptions());
            String choice = scn.next();

            switch (choice) {
                case "1":
                    System.out.println("Type Meld as space-separated tiles (eg. R5 B5 G5): ");
                    System.out.println("Note: if reusing tile from table, specify from which meld it is coming from, then a colon, then the tile (eg. 1:R5) ");
                    String meldStr = scn.next();
                    if (!allTilesExist(meldStr)) {
                        handleInvalidMove(beforeTurn, handBeforeTurn, false);
                        System.out.println("You have tried to use a tile that does not exist - ending turn.");
                        return;
                    }
                    if (meldStr.contains(":") && !hasInitialPoints) {
                        handleInvalidMove(beforeTurn, handBeforeTurn, true);
                        System.out.println("You cannot reuse tiles from table until your initial " + Config.INITIAL_POINTS_THRESHOLD + " points have been played - three tiles have been drawn as penalty.");
                        return;
                    }
                    if (!isValidMeld(meldStr)) {
                        handleInvalidMove(beforeTurn, handBeforeTurn, true);
                        System.out.println("Invalid meld played - three tiles have been drawn as penalty.");
                        return;
                    }
                    if (!game.isValidJokerReplacement(meldStr)) {
                        handleInvalidMove(beforeTurn, handBeforeTurn, true);
                        System.out.println("Joker was not replaced with a tile from your hand - three tiles have been drawn as penalty.");
                        return;
                    }
                    if (!validJokerReuse(meldStr)) {
                        handleInvalidMove(beforeTurn, handBeforeTurn, true);
                        System.out.println("Joker was reused with tiles from the table - three tiles have been drawn as penalty.");
                        return;
                    }
                    if (game.removedFromJokerMeld(meldStr)) {
                        handleInvalidMove(beforeTurn, handBeforeTurn, true);
                        System.out.println("Tile from meld with joker was reused without first replacing the joker - three tiles have been drawn as penalty.");
                        return;
                    }
                    TileCollection played = playMeld(meldStr);
                    meldsPlayed.add(played);
                    mustDrawTile = false;
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
            if (!hasInitialPoints) {
                checkInitialPoints(meldsPlayed);
                //if still not reached initial points, undo moves and draw penalty tiles
                if (meldsPlayed.size() != 0 && !hasInitialPoints) {
                    System.out.println("Your first meld(s) did not reach " + Config.INITIAL_POINTS_THRESHOLD + " points - three tiles have been drawn as penalty.");
                    handleInvalidMove(beforeTurn, handBeforeTurn, true);
                }
            }
            checkTable(beforeTurn, handBeforeTurn, true);
            score = hand.getPoints()*-1;

            if (hand.toString().equals("")) {
                game.setWinner(name);
                game.setScore(playerId, 0);
                game.setOver();
            }
        }
    }

    /* Gets final scores of all players once game is over */
    public String getFinalScores() {
        //int[] scores = clientConnection.receiveScores();
        int[] scores = game.getScores();
        return "GAME OVER. Final Scores:\nPlayer 1: "+ scores[0]*-1 + "\nPlayer 2: " + scores[1]*-1 + "\nPlayer 3: " + scores[2]*-1 + "\n";
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
