package rummikub;

import java.util.ArrayList;
import java.util.Collections;

public class TileCollection {

    private ArrayList<Tile> tiles;

    public TileCollection(ArrayList<Tile> tilesArr) {
        tiles = tilesArr;
    }

    /* Returns true if collection is a valid run, false otherwise */
    public boolean isRun() {

        if (tiles.size() < 3) {
            return false;
        }

        arrangeRun();
        for (int i = 0; i < tiles.size()-1; ++i) {
            //check if all colours equal to first colour
            if (!tiles.get(i).getColour().equals(tiles.get(0).getColour())) {
                return false;
            }
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
        return false;
    }

    /* Returns true if collection is a valid meld, false otherwise */
    public boolean isMeld() {
        return isRun() || isSet();
    }

    /* Returns the total number of points in collection */
    public int getPoints() {
        int pts = 0;
        for (Tile t: tiles) {
            pts += Integer.parseInt(t.getNumber());
        }
        return pts;
    }

    @Override
    public String toString() {
        if (isRun()) {
            arrangeRun();
        } else {
            Collections.sort(tiles);
        }

        String str = "";
        for (Tile t: tiles) {
            str += t.toString() + " ";
        }
        if (isMeld()) {
            str = "{ " + str + "}";
        }
        return str;
    }
}
