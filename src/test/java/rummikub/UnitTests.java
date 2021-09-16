package rummikub;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

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

    @Test
    @DisplayName("creation of runs of tiles")
    public void createTileRuns() {

        //testing basic run
        ArrayList<Tile> testRun = new ArrayList<Tile>();
        testRun.add(new Tile("R","3"));
        testRun.add(new Tile("R","2"));
        testRun.add(new Tile("R","5"));
        testRun.add(new Tile("R","4"));
        TileCollection run = new TileCollection(testRun);
        assertTrue(run.isRun());
        assertFalse(run.isSet());
        assertTrue(run.isMeld());
        assertEquals(14,run.getPoints());
        assertEquals("{ |R2| |R3| |R4| |R5| }", run.toString());

        testRun.remove(1);
        testRun.add(new Tile("R","7"));
        assertFalse(run.isRun());
        assertFalse(run.isSet());
        assertFalse(run.isMeld());
        assertEquals(18,run.getPoints());
        assertEquals("|R2| |R4| |R5| |R7| ", run.toString());

        //testing run with wraparound to Ace (1)
        ArrayList<Tile> testRun2 = new ArrayList<Tile>();
        testRun2.add(new Tile("B","11"));
        testRun2.add(new Tile("B","9"));
        testRun2.add(new Tile("B","10"));
        testRun2.add(new Tile("B","1"));
        testRun2.add(new Tile("B","12"));
        testRun2.add(new Tile("B","13"));
        TileCollection run2 = new TileCollection(testRun2);
        assertTrue(run2.isRun());
        assertFalse(run2.isSet());
        assertTrue(run2.isMeld());
        assertEquals(56,run2.getPoints());
        assertEquals("{ |B9| |B10| |B11| |B12| |B13| |B1| }", run2.toString());

        //testing run with length less than 3
        ArrayList<Tile> testRun3 = new ArrayList<Tile>();
        testRun3.add(new Tile("G","11"));
        testRun3.add(new Tile("G","10"));
        TileCollection run3 = new TileCollection(testRun3);
        assertFalse(run3.isRun());
        assertFalse(run3.isSet());
        assertFalse(run3.isMeld());
        assertEquals(21,run3.getPoints());
        assertEquals("|G10| |G11| ", run3.toString());

        //testing more variations of wraparound runs & edge cases
        ArrayList<Tile> testRun4 = new ArrayList<Tile>();
        testRun4.add(new Tile("R","13"));
        testRun4.add(new Tile("R","2"));
        testRun4.add(new Tile("R","1"));
        TileCollection run4 = new TileCollection(testRun4);
        assertTrue(run4.isRun());
        assertEquals("{ |R13| |R1| |R2| }", run4.toString());

        ArrayList<Tile> testRun5 = new ArrayList<Tile>();
        testRun5.add(new Tile("R","1"));
        testRun5.add(new Tile("R","3"));
        testRun5.add(new Tile("R","2"));
        TileCollection run5 = new TileCollection(testRun5);
        assertTrue(run5.isRun());
        assertEquals("{ |R1| |R2| |R3| }", run5.toString());

        ArrayList<Tile> testRun6 = new ArrayList<Tile>();
        testRun6.add(new Tile("O","1"));
        testRun6.add(new Tile("O","13"));
        testRun6.add(new Tile("O","12"));
        testRun6.add(new Tile("O","3"));
        testRun6.add(new Tile("O","2"));
        TileCollection run6 = new TileCollection(testRun6);
        assertTrue(run6.isRun());
        assertEquals("{ |O12| |O13| |O1| |O2| |O3| }", run6.toString());
    }

}
