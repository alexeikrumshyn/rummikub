package rummikub;

public class Tile {

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
