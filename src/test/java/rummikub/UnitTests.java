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

    @Test
    @DisplayName("creation of sets of tiles")
    public void createTileSets() {

        //testing basic set of 4
        ArrayList<Tile> testSet = new ArrayList<Tile>();
        testSet.add(new Tile("R", "3"));
        testSet.add(new Tile("B", "3"));
        testSet.add(new Tile("G", "3"));
        testSet.add(new Tile("O", "3"));
        TileCollection set = new TileCollection(testSet);
        assertFalse(set.isRun());
        assertTrue(set.isSet());
        assertTrue(set.isMeld());
        assertEquals(12,set.getPoints());
        assertEquals("{ |B3| |G3| |O3| |R3| }", set.toString());

        //testing basic set of 3
        ArrayList<Tile> testSet2 = new ArrayList<Tile>();
        testSet2.add(new Tile("R", "10"));
        testSet2.add(new Tile("B", "10"));
        testSet2.add(new Tile("G", "10"));
        TileCollection set2 = new TileCollection(testSet2);
        assertFalse(set2.isRun());
        assertTrue(set2.isSet());
        assertTrue(set2.isMeld());
        assertEquals(30,set2.getPoints());
        assertEquals("{ |B10| |G10| |R10| }", set2.toString());

        //testing invalid set (different number)
        ArrayList<Tile> testSet3 = new ArrayList<Tile>();
        testSet3.add(new Tile("R", "3"));
        testSet3.add(new Tile("B", "3"));
        testSet3.add(new Tile("G", "3"));
        testSet3.add(new Tile("O", "4"));
        TileCollection set3 = new TileCollection(testSet3);
        assertFalse(set3.isRun());
        assertFalse(set3.isSet());
        assertFalse(set3.isMeld());
        assertEquals(13,set3.getPoints());
        assertEquals("|B3| |G3| |O4| |R3| ", set3.toString());

        //testing invalid set (repeated colour)
        ArrayList<Tile> testSet4 = new ArrayList<Tile>();
        testSet4.add(new Tile("R", "3"));
        testSet4.add(new Tile("B", "3"));
        testSet4.add(new Tile("G", "3"));
        testSet4.add(new Tile("G", "3"));
        TileCollection set4 = new TileCollection(testSet4);
        assertFalse(set4.isRun());
        assertFalse(set4.isSet());
        assertFalse(set4.isMeld());
        assertEquals(12,set4.getPoints());
        assertEquals("|B3| |G3| |G3| |R3| ", set4.toString());

        //testing invalid set (less than 3 tiles)
        ArrayList<Tile> testSet5 = new ArrayList<Tile>();
        testSet5.add(new Tile("R", "3"));
        testSet5.add(new Tile("B", "3"));
        TileCollection set5 = new TileCollection(testSet5);
        assertFalse(set5.isRun());
        assertFalse(set5.isSet());
        assertFalse(set5.isMeld());
        assertEquals(6,set5.getPoints());
        assertEquals("|B3| |R3| ", set5.toString());
    }

    @Test
    @DisplayName("add & remove testing of TileCollection")
    public void addRemoveTileCollection() {

        TileCollection c = new TileCollection();

        //test adds
        c.add(new Tile("B","9"));
        c.add(new Tile("B","5"));
        c.add(new Tile("B","7"));
        c.add(new Tile("B","8"));
        c.add(new Tile("B","6"));

        assertTrue(c.isRun());
        assertFalse(c.isSet());
        assertTrue(c.isMeld());
        assertEquals("{ |B5| |B6| |B7| |B8| |B9| }", c.toString());

        c.add(new Tile("O","8"));
        c.add(new Tile("G","8"));
        c.add(new Tile("R","8"));

        assertFalse(c.isRun());
        assertFalse(c.isSet());
        assertFalse(c.isMeld());
        assertEquals("|B5| |B6| |B7| |B8| |B9| |G8| |O8| |R8| ", c.toString());

        //test removes
        Tile t = c.remove("B6");
        assertEquals("B", t.getColour());
        assertEquals("6", t.getNumber());
        t = c.remove("B9");
        assertEquals("B", t.getColour());
        assertEquals("9", t.getNumber());
        t = c.remove("B7");
        assertEquals("B", t.getColour());
        assertEquals("7", t.getNumber());
        t = c.remove("B5");
        assertEquals("B", t.getColour());
        assertEquals("5", t.getNumber());

        assertFalse(c.isRun());
        assertTrue(c.isSet());
        assertTrue(c.isMeld());
        assertEquals("{ |B8| |G8| |O8| |R8| }", c.toString());

        t = c.remove("G8");
        assertEquals("G", t.getColour());
        assertEquals("8", t.getNumber());
        t = c.remove("B8");
        assertEquals("B", t.getColour());
        assertEquals("8", t.getNumber());
        t = c.remove("R8");
        assertEquals("R", t.getColour());
        assertEquals("8", t.getNumber());
        t = c.remove("O8");
        assertEquals("O", t.getColour());
        assertEquals("8", t.getNumber());

        assertFalse(c.isRun());
        assertFalse(c.isSet());
        assertFalse(c.isMeld());
        assertEquals("", c.toString());
    }

}
