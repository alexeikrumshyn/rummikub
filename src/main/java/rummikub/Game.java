package rummikub;

import java.io.Serializable;
import java.util.ArrayList;
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
        //assume for now that all tiles come from the player's hand
        for (Tile t : fromHand)
            t.setSource("hand");
        TileCollection newMeld = new TileCollection(fromHand);
        addMeldToTable(newMeld);
        return newMeld;
    }

    /* Removes Tile corresponding to String str from table */
    public Tile removeTileFromTable(int meldNumber, String str) {
        return table.get(meldNumber-1).remove(str);
    }

}
