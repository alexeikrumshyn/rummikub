package rummikub;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

public class UnitTests {

    @Test
    @DisplayName("creation of individual tiles")
    public void createTiles() {
        Tile tile = new Tile("B","1");
        assertEquals("B", tile.getColour());
        assertEquals("1", tile.getNumber());
        assertEquals("neutral", tile.getSource());
        assertEquals("|B1|", tile.toString());

        tile = new Tile("G","13");
        tile.setSource("hand");
        assertEquals("G", tile.getColour());
        assertEquals("13", tile.getNumber());
        assertEquals("hand", tile.getSource());
        assertEquals("*|G13|", tile.toString());


        tile = new Tile("B","4");
        tile.setSource("table");
        assertEquals("B", tile.getColour());
        assertEquals("4", tile.getNumber());
        assertEquals("table", tile.getSource());
        assertEquals("!|B4|", tile.toString());
    }

}
