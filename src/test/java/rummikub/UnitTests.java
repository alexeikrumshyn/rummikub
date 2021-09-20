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
        testSet.add(new Tile("G", "3"));
        testSet.add(new Tile("B", "3"));
        testSet.add(new Tile("O", "3"));
        TileCollection set = new TileCollection(testSet);
        assertFalse(set.isRun());
        assertTrue(set.isSet());
        assertTrue(set.isMeld());
        assertEquals(12,set.getPoints());
        assertEquals("{ |R3| |B3| |G3| |O3| }", set.toString());

        //testing basic set of 3
        ArrayList<Tile> testSet2 = new ArrayList<Tile>();
        testSet2.add(new Tile("B", "10"));
        testSet2.add(new Tile("G", "10"));
        testSet2.add(new Tile("R", "10"));
        TileCollection set2 = new TileCollection(testSet2);
        assertFalse(set2.isRun());
        assertTrue(set2.isSet());
        assertTrue(set2.isMeld());
        assertEquals(30,set2.getPoints());
        assertEquals("{ |R10| |B10| |G10| }", set2.toString());

        //testing invalid set (different number)
        ArrayList<Tile> testSet3 = new ArrayList<Tile>();
        testSet3.add(new Tile("O", "4"));
        testSet3.add(new Tile("R", "3"));
        testSet3.add(new Tile("G", "3"));
        testSet3.add(new Tile("B", "3"));
        TileCollection set3 = new TileCollection(testSet3);
        assertFalse(set3.isRun());
        assertFalse(set3.isSet());
        assertFalse(set3.isMeld());
        assertEquals(13,set3.getPoints());
        assertEquals("|R3| |B3| |G3| |O4| ", set3.toString());

        //testing invalid set (repeated colour)
        ArrayList<Tile> testSet4 = new ArrayList<Tile>();
        testSet4.add(new Tile("G", "3"));
        testSet4.add(new Tile("B", "3"));
        testSet4.add(new Tile("R", "3"));
        testSet4.add(new Tile("G", "3"));
        TileCollection set4 = new TileCollection(testSet4);
        assertFalse(set4.isRun());
        assertFalse(set4.isSet());
        assertFalse(set4.isMeld());
        assertEquals(12,set4.getPoints());
        assertEquals("|R3| |B3| |G3| |G3| ", set4.toString());

        //testing invalid set (less than 3 tiles)
        ArrayList<Tile> testSet5 = new ArrayList<Tile>();
        testSet5.add(new Tile("B", "3"));
        testSet5.add(new Tile("R", "3"));
        TileCollection set5 = new TileCollection(testSet5);
        assertFalse(set5.isRun());
        assertFalse(set5.isSet());
        assertFalse(set5.isMeld());
        assertEquals(6,set5.getPoints());
        assertEquals("|R3| |B3| ", set5.toString());
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
        assertEquals("|R8| |B5| |B6| |B7| |B8| |B9| |G8| |O8| ", c.toString());

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
        assertEquals("{ |R8| |B8| |G8| |O8| }", c.toString());

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

    @Test
    @DisplayName("creation of new game")
    public void createGame() {

        Game game = new Game(); //Game constructor will populate the stock and initialize the table

        String expected =   "|R1| |R1| |R2| |R2| |R3| |R3| |R4| |R4| |R5| |R5| |R6| |R6| |R7| |R7| |R8| |R8| |R9| |R9| |R10| |R10| |R11| |R11| |R12| |R12| |R13| |R13| " +
                            "|B1| |B1| |B2| |B2| |B3| |B3| |B4| |B4| |B5| |B5| |B6| |B6| |B7| |B7| |B8| |B8| |B9| |B9| |B10| |B10| |B11| |B11| |B12| |B12| |B13| |B13| " +
                            "|G1| |G1| |G2| |G2| |G3| |G3| |G4| |G4| |G5| |G5| |G6| |G6| |G7| |G7| |G8| |G8| |G9| |G9| |G10| |G10| |G11| |G11| |G12| |G12| |G13| |G13| " +
                            "|O1| |O1| |O2| |O2| |O3| |O3| |O4| |O4| |O5| |O5| |O6| |O6| |O7| |O7| |O8| |O8| |O9| |O9| |O10| |O10| |O11| |O11| |O12| |O12| |O13| |O13| ";
        //test that stock was created properly

        assertEquals(expected, game.getStock());

        //test that table is initially empty
        assertEquals("", game.getTable());

    }

    @Test
    @DisplayName("remove from stock and add & remove testing of table")
    public void manipulateStockAndTable() {

        Game game = new Game(); //Game constructor will populate the stock and initialize the table

        //test "drawing" tiles from stock
        Tile removed = game.removeFromStock(103);
        assertEquals("|O13|",removed.toString());
        String expected =   "|R1| |R1| |R2| |R2| |R3| |R3| |R4| |R4| |R5| |R5| |R6| |R6| |R7| |R7| |R8| |R8| |R9| |R9| |R10| |R10| |R11| |R11| |R12| |R12| |R13| |R13| " +
                            "|B1| |B1| |B2| |B2| |B3| |B3| |B4| |B4| |B5| |B5| |B6| |B6| |B7| |B7| |B8| |B8| |B9| |B9| |B10| |B10| |B11| |B11| |B12| |B12| |B13| |B13| " +
                            "|G1| |G1| |G2| |G2| |G3| |G3| |G4| |G4| |G5| |G5| |G6| |G6| |G7| |G7| |G8| |G8| |G9| |G9| |G10| |G10| |G11| |G11| |G12| |G12| |G13| |G13| " +
                            "|O1| |O1| |O2| |O2| |O3| |O3| |O4| |O4| |O5| |O5| |O6| |O6| |O7| |O7| |O8| |O8| |O9| |O9| |O10| |O10| |O11| |O11| |O12| |O12| |O13| ";
        assertEquals(expected, game.getStock());

        removed = game.removeFromStock(0);
        assertEquals("|R1|",removed.toString());
        expected =   "|R1| |R2| |R2| |R3| |R3| |R4| |R4| |R5| |R5| |R6| |R6| |R7| |R7| |R8| |R8| |R9| |R9| |R10| |R10| |R11| |R11| |R12| |R12| |R13| |R13| " +
                "|B1| |B1| |B2| |B2| |B3| |B3| |B4| |B4| |B5| |B5| |B6| |B6| |B7| |B7| |B8| |B8| |B9| |B9| |B10| |B10| |B11| |B11| |B12| |B12| |B13| |B13| " +
                "|G1| |G1| |G2| |G2| |G3| |G3| |G4| |G4| |G5| |G5| |G6| |G6| |G7| |G7| |G8| |G8| |G9| |G9| |G10| |G10| |G11| |G11| |G12| |G12| |G13| |G13| " +
                "|O1| |O1| |O2| |O2| |O3| |O3| |O4| |O4| |O5| |O5| |O6| |O6| |O7| |O7| |O8| |O8| |O9| |O9| |O10| |O10| |O11| |O11| |O12| |O12| |O13| ";
        assertEquals(expected, game.getStock());

        removed = game.removeFromStock(1);
        assertEquals("|R2|",removed.toString());
        expected =   "|R1| |R2| |R3| |R3| |R4| |R4| |R5| |R5| |R6| |R6| |R7| |R7| |R8| |R8| |R9| |R9| |R10| |R10| |R11| |R11| |R12| |R12| |R13| |R13| " +
                "|B1| |B1| |B2| |B2| |B3| |B3| |B4| |B4| |B5| |B5| |B6| |B6| |B7| |B7| |B8| |B8| |B9| |B9| |B10| |B10| |B11| |B11| |B12| |B12| |B13| |B13| " +
                "|G1| |G1| |G2| |G2| |G3| |G3| |G4| |G4| |G5| |G5| |G6| |G6| |G7| |G7| |G8| |G8| |G9| |G9| |G10| |G10| |G11| |G11| |G12| |G12| |G13| |G13| " +
                "|O1| |O1| |O2| |O2| |O3| |O3| |O4| |O4| |O5| |O5| |O6| |O6| |O7| |O7| |O8| |O8| |O9| |O9| |O10| |O10| |O11| |O11| |O12| |O12| |O13| ";
        assertEquals(expected, game.getStock());

        //test adding melds to the table
        TileCollection meld1 = new TileCollection();
        meld1.add(new Tile("G","3"));
        meld1.add(new Tile("B","3"));
        meld1.add(new Tile("O","3"));
        meld1.add(new Tile("R","3"));
        game.addMeldToTable(meld1);
        expected = "{ |R3| |B3| |G3| |O3| }" + "\n";
        assertEquals(expected, game.getTable());

        TileCollection meld2 = new TileCollection();
        meld2.add(new Tile("R","7"));
        meld2.add(new Tile("R","5"));
        meld2.add(new Tile("R","6"));
        meld2.add(new Tile("R","4"));
        game.addMeldToTable(meld2);
        expected = "{ |R3| |B3| |G3| |O3| }" + "\n" + "{ |R4| |R5| |R6| |R7| }" + "\n";
        assertEquals(expected, game.getTable());

        TileCollection meld3 = new TileCollection();
        meld3.add(new Tile("O","12"));
        meld3.add(new Tile("O","2"));
        meld3.add(new Tile("O","1"));
        meld3.add(new Tile("O","13"));
        game.addMeldToTable(meld3);
        expected = "{ |R3| |B3| |G3| |O3| }" + "\n" + "{ |R4| |R5| |R6| |R7| }" + "\n" + "{ |O12| |O13| |O1| |O2| }" + "\n";
        assertEquals(expected, game.getTable());

        //test removing tiles
        removed = game.removeTileFromTable(1, "O3");
        assertEquals("|O3|", removed.toString());
        expected = "{ |R3| |B3| |G3| }" + "\n" + "{ |R4| |R5| |R6| |R7| }" + "\n" + "{ |O12| |O13| |O1| |O2| }" + "\n";
        assertEquals(expected, game.getTable());

        removed = game.removeTileFromTable(2, "R4");
        assertEquals("|R4|", removed.toString());
        expected = "{ |R3| |B3| |G3| }" + "\n" + "{ |R5| |R6| |R7| }" + "\n" + "{ |O12| |O13| |O1| |O2| }" + "\n";
        assertEquals(expected, game.getTable());

        removed = game.removeTileFromTable(3, "O2");
        assertEquals("|O2|", removed.toString());
        expected = "{ |R3| |B3| |G3| }" + "\n" + "{ |R5| |R6| |R7| }" + "\n" + "{ |O12| |O13| |O1| }" + "\n";
        assertEquals(expected, game.getTable());
    }

}
