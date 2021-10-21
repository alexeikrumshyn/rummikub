package rummikub;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Game implements Serializable {

    private TileCollection stock;
    private ArrayList<TileCollection> table;
    private boolean isOver;
    private String winner;
    private int[] scores;

    public Game() {
        createStock();
        table = new ArrayList<>();
        isOver = false;
        scores = new int[]{0, 0, 0};
    }

    /* Deep copy constructor */
    public Game(Game g) {
        stock = new TileCollection(g.stock);

        table = new ArrayList<>();
        for (int i = 0; i < g.table.size(); ++i)
            table.add(new TileCollection(g.table.get(i)));

        isOver = g.isOver;
        scores = new int[]{0, 0, 0};
        for (int i = 0; i < g.scores.length; ++i)
            scores[i] = g.scores[i];
    }

    /* Creates the initial collection of tiles to draw from */
    public void createStock() {
        ArrayList<String> colours = new ArrayList<>();
        colours.add("B");
        colours.add("G");
        colours.add("O");
        colours.add("R");

        stock = new TileCollection();
        for (String clr : colours) {
            for (int i = 1; i <= 13; ++i) {
                stock.add(new Tile(clr,Integer.toString(i)));
                stock.add(new Tile(clr,Integer.toString(i)));
            }
        }
        stock.add(new Tile("*","*"));
        stock.add(new Tile("*","*"));
    }

    /* Returns true if the game is over, false otherwise */
    public boolean isOver() {
        return isOver;
    }

    /* Sets over to be true - end of game */
    public void setOver() {
        isOver = true;
    }

    /* Returns a string representation of the stock */
    public String getStock() {
        return stock.toString();
    }

    /* Returns a string representation of the table */
    public String getTable() {

        String str = "";
        for (TileCollection c : table) {
            str += c.toString();
            str += "\n";
        }
        return str;
    }

    /* Returns true if meld at idx on table contains tile corresponding to str, false otherwise */
    public boolean tableContains(int idx, String str) {
        if (idx > table.size()-1)
            return false;
        return table.get(idx).contains(str);
    }

    /* Gets the winner of the Game */
    public String getWinner() {
        return winner;
    }

    /* Sets the winner of the Game */
    public void setWinner(String n) {
        winner = n;
    }

    /* Gets the scores of all players */
    public int[] getScores() {
        return scores;
    }

    /* Sets the score of player p to s */
    public void setScore(int p, int s) {
        scores[p-1] = s;
    }

    /* Removes, then returns the Tile at index idx from the stock */
    public Tile removeFromStock(int idx) {
        return stock.remove(idx);
    }

    /* Removes, then returns random Tile from the stock */
    public Tile removeFromStock() {
        Random rand = new Random();
        int rndNum = rand.nextInt(stock.getSize());
        return removeFromStock(rndNum);
    }

    /* Removes, then returns Tile corresponding to string str from the stock */
    public Tile removeFromStock(String str) {
        return stock.remove(str);
    }

    /* Adds given meld c to table */
    public void addMeldToTable(TileCollection meld) {
        table.add(meld);
    }

    /* Creates new meld based on tiles from player's hand and table, then adds it to the table, then returns the new meld */
    public TileCollection createMeld(ArrayList<Tile> fromHand, ArrayList<String> fromTable) {

        boolean checkBrokenRun = false;

        for (Tile t : fromHand)
            t.setSource("hand");

        ArrayList<Tile> tilesFromTable = new ArrayList<>();
        for (String str : fromTable) {
            String[] splitStr = str.split(":");
            if (table.get(Integer.parseInt(splitStr[0])-1).isRun())
                checkBrokenRun = true;

            Tile reusedTile = removeTileFromTable(Integer.parseInt(splitStr[0]), splitStr[1]);
            tilesFromTable.add(reusedTile);
            if (checkBrokenRun)
                checkBrokenRun(Integer.parseInt(splitStr[0])-1);
        }

        fixEmptyMelds();

        for (Tile t : tilesFromTable)
            t.setSource("table");

        fromHand.addAll(tilesFromTable);

        TileCollection newMeld = new TileCollection(fromHand);
        addMeldToTable(newMeld);
        return newMeld;
    }

    /* Removes Tile corresponding to String str from table */
    public Tile removeTileFromTable(int meldNumber, String str) {
        return table.get(meldNumber-1).remove(str);
    }

    /* Resets all tiles on table to be from neutral source */
    public void resetTableTileSources() {
        for (TileCollection c : table) {
            c.resetTileSources();
        }
    }

    /* Checks if tile on table reused breaks up a run, and adjusts table */
    public void checkBrokenRun(int meldNumber) {

        TileCollection affectedRun = table.get(meldNumber);

        //first, check if still is run (case where either first or last tile was reused): do nothing
        if (affectedRun.isRun())
            return;

        //case where taken from the middle: iterate until broken
        TileCollection leftRun = new TileCollection();
        for (int i = 0; i < affectedRun.getSize(); ++i) {
            Tile currentTile = affectedRun.getTile(i);
            leftRun.add(currentTile);
            //check when it is no longer a valid run
            if (leftRun.getSize() >= 3 && !leftRun.isRun()) {
                leftRun.remove(currentTile.getColour()+currentTile.getNumber());
                table.add(leftRun);
                for (int j = 0; j < i; ++j) {
                    affectedRun.remove(0);
                }
                return;
            }
        }
    }

    /* Checks if any melds on table are empty, and deletes if so */
    public void fixEmptyMelds() {
        ArrayList<TileCollection> empties = new ArrayList<>();
        for (TileCollection c : table) {
            if (c.getSize() == 0) {
                empties.add(c);
            }
        }
        for (TileCollection c : empties) {
            table.remove(c);
        }
    }

    /* Checks joker replacement given user play */
    public boolean isValidJokerReplacement(String play) {
        String[] playedTiles = play.split("\\s+");
        HashSet<Integer> affectedMelds = new HashSet<>(); //used to eliminate duplicates (checking same meld twice)
        //get all melds tiles were reused from
        for (int i = 0; i < playedTiles.length; ++i) {
            if (playedTiles[i].contains(":")) {
                String[] reused = playedTiles[i].split(":");
                affectedMelds.add(Integer.parseInt(reused[0]));
            }
        }
        //for each affected meld in play, find which one contains the joker and at which pos the joker is found
        int jokerPos = -1;
        for (Integer meldNum : affectedMelds) {
            TileCollection currMeld = table.get(meldNum-1);
            //case joker was in this meld, then get its position
            if (currMeld.countJokers() == 1 && currMeld.getSize() != 1) {
                for (int i = 0; i < currMeld.getSize(); ++i) {
                    if (currMeld.getTile(i).getColour().equals("*")) {
                        jokerPos = i;
                        break;
                    }
                }
            }
        }
        //case no melds with jokers affected, then no replacement to check
        if (jokerPos == -1)
            return true;

        //for each affected meld, check the tile replacing it was not reused from the table
        for (Integer meldNum : affectedMelds) {
            TileCollection currMeld = table.get(meldNum-1);
            //case this meld contains replacement for joker
            if (currMeld.countJokers() == 0 && playedTiles[jokerPos].contains(":"))
                    return false;
        }

        return true;
    }

    /* Checks if melds on table are valid, and returns tiles that are part of invalid melds. If performFix, then fix the table. */
    public ArrayList<Tile> checkTable(boolean performFix) {
        ArrayList<Tile> invalidTiles = new ArrayList<>();
        for (TileCollection c : table) {
            if (!c.isMeld()) {
                for (int i = 0; i < c.getSize(); ++i)
                    if (performFix)
                        invalidTiles.add(c.remove(0));
                    else
                        invalidTiles.add(c.getTile(0));
            }
        }
        fixEmptyMelds();
        return invalidTiles;
    }

    /* Checks if tile was taken from meld with joker */
    public boolean removedFromJokerMeld(String play) {
        String[] playedTiles = play.split("\\s+");

        //if removed just one tile from meld with joker, invalid (i.e. did not replace joker before taking a tile)
        //this has to be true because at least 2 tiles must be reused from the meld to replace the joker
        for (String tileStr : playedTiles) {
            if (tileStr.contains(":")) {
                String[] splitStr = tileStr.split(":");
                int meldNum = Integer.parseInt(splitStr[0]);
                TileCollection meld = table.get(meldNum-1);
                if (meld.countJokers() > 0 && meld.getSize() > 1) {
                    //count tiles taken from this meld in play
                    int count = (play.split(meldNum+":")).length-1;
                    if (count == 1)
                        return true;
                }
            }
        }
        return false;
    }
}
