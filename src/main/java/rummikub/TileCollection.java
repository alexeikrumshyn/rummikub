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

    /* Deep copy constructor */
    public TileCollection(TileCollection tc) {
        checkIfMeld = tc.checkIfMeld;
        tiles = new ArrayList<>();
        for (int i = 0; i < tc.getSize(); ++i)
            tiles.add(new Tile(tc.getTile(i)));
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
        //special case of two jokers with one tile - consider it a set
        if (countJokers() == 2 && tiles.size() == 3)
            return false;

        //check for first colour in set (could be a joker)
        int firstClr = 0;
        for (Tile t : tiles) {
            if (t.getColour().equals("*")) firstClr++;
            else break;
        }
        //check if all colours equal to first colour
        for (int i = 0; i < tiles.size(); ++i) {
            if (!tiles.get(i).getColour().equals(tiles.get(firstClr).getColour()) && !tiles.get(i).getColour().equals("*")) {
                return false;
            }
        }

        arrangeRun();
        for (int i = 0; i < tiles.size()-1; ++i) {

            String nextNumStr = tiles.get(i+1).getNumber();
            String currentNumStr = tiles.get(i).getNumber();

            //check number two indices later in joker case
            if (i < tiles.size()-2 && nextNumStr.equals("*") && !currentNumStr.equals("*") && !tiles.get(i+2).getNumber().equals("*")) {
                int currentNum = Integer.parseInt(currentNumStr);
                int nextNextNum = Integer.parseInt(tiles.get(i+2).getNumber());
                int diff = Math.abs((nextNextNum - currentNum)%13);
                if (diff != 11 && diff != 2) {
                    return false;
                }
            }
            //check number three indices later in back-to-back joker case (e.g. R2 * * R5)
            if (i < tiles.size()-3 && nextNumStr.equals("*")) {
                if (tiles.get(i+2).getNumber().equals("*")) {
                    int currentNum = Integer.parseInt(currentNumStr);
                    int nextNextNextNum = Integer.parseInt(tiles.get(i + 3).getNumber());
                    int diff = Math.abs((nextNextNextNum - currentNum) % 13);
                    if (diff != 10 && diff != 3) {
                        return false;
                    }
                }
            }
            if (currentNumStr.equals("*") || nextNumStr.equals("*")) {
                continue;
            }

            //check if numbers are sequential - considering special case of 13 (King) wrapping around to 1 (Ace)
            int nextNum = Integer.parseInt(nextNumStr);
            int currentNum = Integer.parseInt(currentNumStr);

            if (Math.abs((nextNum - currentNum)%13) != 1 && !(nextNum == 1 && currentNum == 13)) {
                return false;
            }
        }
        return true;
    }

    /* Orders tiles to accommodate runs that wrap around (such as Q K A) and joker cases */
    public void arrangeRun() {

        //preserve jokers at beginning if they started there
        int jokersAtBeginning = 0;
        for (int i = 0; i < tiles.size(); ++i) {
            if (tiles.get(i).getNumber().equals("*"))
                jokersAtBeginning++;
            else
                break;
        }
        Collections.sort(tiles);
        //put jokers back to beginning after sort
        for (int i = 0; i < jokersAtBeginning; ++i)
            tiles.add(0, tiles.remove(tiles.size() - 1));

        //place joker(s) in right spot
        if (countJokers() > 0 && countJokers() > jokersAtBeginning) {
            for (int i = jokersAtBeginning; i < tiles.size()-1; ++i) {
                if (tiles.get(i+1).getNumber().equals("*"))
                    break;
                int nextNum = Integer.parseInt(tiles.get(i+1).getNumber());
                int currentNum = Integer.parseInt(tiles.get(i).getNumber());
                if (Math.abs((nextNum - currentNum)%13) != 1 && tiles.get(tiles.size()-1).getNumber().equals("*")) {
                    tiles.add(i + 1, tiles.remove(tiles.size() - 1));
                    //check back-to-back joker case
                    if (i < tiles.size()-2) {
                        int nextNextNum = Integer.parseInt(tiles.get(i + 2).getNumber());
                        if (Math.abs((nextNextNum - currentNum)%13) != 2 && tiles.get(tiles.size()-1).getNumber().equals("*"))
                            tiles.add(i + 2, tiles.remove(tiles.size() - 1));
                    }
                    break;
                }
            }
        }
        //wraparound cases
        boolean normal_case = (tiles.get(tiles.size()-1).getNumber().equals("13") && tiles.get(0).getNumber().equals("1") && tiles.size() != 13);
        if (normal_case) {
            for (int i = 0; i < tiles.size()-1; ++i) {
                tiles.add(tiles.remove(0)); //move first tile to back
                if (tiles.get(0).getNumber().equals("*") || tiles.get(tiles.size()-1).getNumber().equals("*")) {
                    break;
                }
                if (Math.abs(Integer.parseInt(tiles.get(0).getNumber()) - Integer.parseInt(tiles.get(tiles.size()-1).getNumber())%13) != 1) {
                    break;
                }
            }
        }
        boolean joker_case = (tiles.get(0).getNumber().equals("2") && countJokers() == 1 && tiles.get(tiles.size()-1).getNumber().equals("13")); //e.g. K * 2 is initially sorted as 2 K *
        if (joker_case) {
            for (int i = 0; i < tiles.size()-1; ++i) {
                tiles.add(tiles.remove(0)); //move first tile to back
                if (tiles.get(0).getNumber().equals("*")) {
                    tiles.add(tiles.size()-2-i, tiles.remove(0));
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

        //special case of two jokers with one tile - consider it a set
        if (countJokers() == 2 && tiles.size() == 3)
            return true;

        //check for first number in set (could be a joker)
        int firstClr = 0;
        for (Tile t : tiles) {
            if (t.getColour().equals("*")) firstClr++;
            else break;
        }

        for (int i = 0; i < tiles.size(); ++i) {
            //check if all numbers equal to first number
            if (!tiles.get(i).getNumber().equals(tiles.get(firstClr).getNumber()) && !tiles.get(i).getNumber().equals("*")) {
                return false;
            }
            //check if all colours unique
            for (int j = i+1; j < tiles.size(); ++j) {
                if (tiles.get(i).getColour().equals(tiles.get(j).getColour()) && !tiles.get(i).getColour().equals("*") && !tiles.get(j).getColour().equals("*")) {
                    return false;
                }
            }
        }
        return true;
    }

    /* Orders tiles to accommodate sets with jokers */
    public void arrangeSet() {
        if (countJokers() == 0)
            return;
        Collections.sort(tiles);
        ArrayList<String> colourOrder = new ArrayList<>();
        colourOrder.add("R");
        colourOrder.add("B");
        colourOrder.add("G");
        colourOrder.add("O");

        for (int i = 0; i < tiles.size(); ++i) {
            if (!tiles.get(i).getColour().equals(colourOrder.get(i)) && tiles.get(tiles.size()-1).getColour().equals("*")) {
                tiles.add(i, tiles.remove(tiles.size() - 1));
            }
        }
    }

    /* Returns number of jokers in collection */
    public int countJokers() {
        int jokerCounter = 0;
        for (Tile t : tiles) {
            if (t.getNumber().equals("*") && t.getColour().equals("*"))
                jokerCounter++;
        }
        return jokerCounter;
    }

    /* Returns true if collection is a valid meld, false otherwise */
    public boolean isMeld() {
        return isRun() || isSet();
    }

    /* Returns the total number of points in collection */
    public int getPoints() {
        int pts = 0;
        for (int i = 0; i < tiles.size(); ++i) {
            String tileNumStr = tiles.get(i).getNumber();
            //check for joker
            if (tileNumStr.equals("*")) {
                if (isSet()) {
                    for (Tile tile : tiles)
                        if (!tile.getNumber().equals("*")) { pts += Integer.parseInt(tile.getNumber()); break; }
                } else if (isRun()) {
                    if (tiles.get(i).getNumber().equals("*")) {
                        //look before
                        if (i != 0 && !tiles.get(i-1).getNumber().equals("*"))
                            pts += Integer.parseInt(tiles.get(i-1).getNumber()) + 1;
                        //look after
                        else if (i != tiles.size()-1 && !tiles.get(i+1).getNumber().equals("*"))
                            pts += Integer.parseInt(tiles.get(i+1).getNumber()) - 1;
                    } else {
                        pts += Integer.parseInt(tiles.get(i).getNumber());
                    }
                }
            } else {
                int tileNum = Integer.parseInt(tileNumStr);
                if (tileNum > 10)
                    pts += 10;
                else
                    pts += tileNum;
            }
        }
        return pts;
    }

    /* Adds given Tile t to the collection */
    public void add(Tile t) {
        tiles.add(t);
    }

    /* Removes Tile corresponding to str (for example: "G8") from the collection */
    public Tile remove(String str) {

        String clr;
        String num;
        //check for joker
        if (str.equals("*")) {
            clr = "*";
            num = "*";
        } else{
            clr = str.substring(0, 1);
            num = str.substring(1);
        }
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
        } else if (checkIfMeld && isSet()){
            arrangeSet();
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
