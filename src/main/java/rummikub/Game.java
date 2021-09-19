package rummikub;

import java.util.ArrayList;

public class Game {

    private TileCollection stock;
    private ArrayList<TileCollection> table;

    public Game() {
        createStock();
        table = new ArrayList<>();
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

    /* Removes, then returns the Tile at index idx from the stock */
    public Tile removeFromStock(int idx) {
        return stock.remove(idx);
    }

    /* Adds given meld c to table */
    public void addMeldToTable(TileCollection meld) {
        table.add(meld);
    }

    /* Removes Tile corresponding to String str from table */
    public Tile removeTileFromTable(int meldNumber, String str) {
        return table.get(meldNumber-1).remove(str);
    }

}
