package rummikub;

import java.io.Serializable;
import java.util.ArrayList;

public class Tile  implements Comparable<Tile>, Serializable {

    private String colour;
    private String number;
    private String source;

    public Tile(String clr, String num) {
        colour = clr;
        number = num;
        source = "neutral";
    }

    public String getColour() {
        return colour;
    }

    public String getNumber() {
        return number;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String src) {
        this.source = src;
    }

    @Override
    public int compareTo(Tile t2) {
        if (this.getColour().equals(t2.getColour())) {
            if (Integer.parseInt(this.getNumber()) < Integer.parseInt(t2.getNumber())) {
                return -1;
            } else {
                return 1;
            }
        } else {
            ArrayList<String> colourOrder = new ArrayList<String>();
            colourOrder.add("R");
            colourOrder.add("B");
            colourOrder.add("G");
            colourOrder.add("O");

            if (colourOrder.indexOf(this.getColour()) < colourOrder.indexOf(t2.getColour())) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    @Override
    public String toString() {
        String str = "";
        if (source == "hand") {
            str += "*";
        } else if (source == "table") {
            str += "!";
        }
        str += "|" + getColour() + getNumber() + "|";
        return str;
    }
}
