package rummikub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class TileCollection implements Serializable {

    private ArrayList<Tile> tiles;
    public boolean checkIfMeld;

    public TileCollection() {
        tiles = new ArrayList<>();
        checkIfMeld = true;
    }

    public TileCollection(ArrayList<Tile> tilesArr) {
        tiles = tilesArr;
        checkIfMeld = true;
    }

    public TileCollection(String str) {
        tiles = new ArrayList<>();
        String[] tilesStr = str.split("\\s+");
        for (String tileStr : tilesStr) {
            if (tileStr.equals("*")) {
                this.add(new Tile("*", "*"));
            } else {
                String clr = tileStr.substring(0, 1);
                String num = tileStr.substring(1);
                this.add(new Tile(clr,num));
            }
        }
        checkIfMeld = true;
    }

    public int getSize() {
        return tiles.size();
    }

    /* Returns tile at position idx */
    public Tile getTile(int idx) {
        if (checkIfMeld && isRun()) {
            arrangeRun();
        } else {
            Collections.sort(tiles);
        }
        return tiles.get(idx);
    }

    /* Returns true if collection is a valid run, false otherwise */
    public boolean isRun() {

        if (tiles.size() < 3) {
            return false;
        }

        //check if all colours equal to first colour
        for (int i = 0; i < tiles.size(); ++i) {
            if (!tiles.get(i).getColour().equals(tiles.get(0).getColour())) {
                return false;
            }
        }

        arrangeRun();
        for (int i = 0; i < tiles.size()-1; ++i) {
            //check if numbers are sequential - considering special case of 13 (King) wrapping around to 1 (Ace)
            int nextNum = Integer.parseInt(tiles.get(i+1).getNumber());
            int currentNum = Integer.parseInt(tiles.get(i).getNumber());

            if (Math.abs((nextNum - currentNum)%13) != 1 && !(nextNum == 1 && currentNum == 13)) {
                return false;
            }
        }
        return true;
    }

    /* Orders tiles to accommodate runs that wrap around (such as Q K A) */
    public void arrangeRun() {
        Collections.sort(tiles);
        if (tiles.get(tiles.size()-1).getNumber().equals("13") && tiles.get(0).getNumber().equals("1")) {
            for (int i = 0; i < tiles.size()-1; ++i) {
                tiles.add(tiles.remove(0)); //move first tile to back
                if (Math.abs(Integer.parseInt(tiles.get(0).getNumber()) - Integer.parseInt(tiles.get(tiles.size()-1).getNumber())%13) != 1) {
                    break;
                }
            }
        }
    }

    /* Returns true if collection is valid set, false otherwise */
    public boolean isSet() {

        if (tiles.size() < 3 || tiles.size() > 4) {
            return false;
        }

        for (int i = 0; i < tiles.size(); ++i) {
            //check if all numbers equal to first number
            if (!tiles.get(i).getNumber().equals(tiles.get(0).getNumber())) {
                return false;
            }
            //check if all colours unique
            for (int j = i+1; j < tiles.size(); ++j) {
                if (tiles.get(i).getColour().equals(tiles.get(j).getColour())) {
                    return false;
                }
            }
        }
        return true;
    }

    /* Returns true if collection is a valid meld, false otherwise */
    public boolean isMeld() {
        return isRun() || isSet();
    }

    /* Returns the total number of points in collection */
    public int getPoints() {
        int pts = 0;
        for (Tile t: tiles) {
            int tileNum = Integer.parseInt(t.getNumber());
            if (tileNum > 10)
                pts += 10;
            else
                pts += tileNum;
        }
        return pts;
    }

    /* Adds given Tile t to the collection */
    public void add(Tile t) {
        tiles.add(t);
    }

    /* Removes Tile corresponding to str (for example: "G8") from the collection */
    public Tile remove(String str) {
        //first, parse str into colour and number
        String clr = str.substring(0,1);
        String num = str.substring(1);
        for (Tile t: tiles) {
            if (t.getColour().equals(clr) && t.getNumber().equals(num)) {
                Tile toRm = t;
                tiles.remove(t);
                return toRm;
            }
        }
        return null;
    }

    /* Removes Tile at index idx from the collection */
    public Tile remove(int idx) {
        if (checkIfMeld && isRun()) {
            arrangeRun();
        } else {
            Collections.sort(tiles);
        }
        return tiles.remove(idx);
    }

    @Override
    public String toString() {
        if (checkIfMeld && isRun()) {
            arrangeRun();
        } else {
            Collections.sort(tiles);
        }

        String str = "";
        for (Tile t: tiles) {
            str += t.toString() + " ";
        }
        if (checkIfMeld && isMeld()) {
            str = "{ " + str + "}";
        }
        return str;
    }
}
