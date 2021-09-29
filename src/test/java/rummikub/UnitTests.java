package rummikub;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
        assertEquals(50,run2.getPoints());
        assertEquals("{ |B9| |B10| |B11| |B12| |B13| |B1| }", run2.toString());

        //testing run with length less than 3
        ArrayList<Tile> testRun3 = new ArrayList<Tile>();
        testRun3.add(new Tile("G","11"));
        testRun3.add(new Tile("G","10"));
        TileCollection run3 = new TileCollection(testRun3);
        assertFalse(run3.isRun());
        assertFalse(run3.isSet());
        assertFalse(run3.isMeld());
        assertEquals(20,run3.getPoints());
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

    @Test
    @DisplayName("test player sequence and UI updates")
    public void playerSequenceAndUI() throws InterruptedException, IOException {

        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R12");
                player1.drawTile("B12");
                player1.drawTile("O1");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R12| |B12| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //simulate p1 draws tile before turn ends, so we can rig which tile p1 gets
                player1.drawTile("G12");

                //prompt p1 for action (end turn)
                String inString = "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after ending turn
                expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R12| |B12| |G12| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |R13| |B13| |G13| }\n" +
                        "{ |R2| |G2| |O2| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R12| |B12| |G12| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (play meld, then end turn)
                inString = "1\n" + "R12 B12 G12\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |R13| |B13| |G13| }\n" +
                        "{ |R2| |G2| |O2| }\n" +
                        "{ *|R12| *|B12| *|G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
            }});

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("R12");
                player2.drawTile("R11");
                player2.drawTile("R13");
                player2.drawTile("B1");

                //test p2's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R11| |R12| |R13| |B1| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|R12| *|R13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|B1| " + "\n\n";
                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();

                player2.updateGame();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |R13| |B13| |G13| }\n" +
                        "{ |R2| |G2| |O2| }\n" +
                        "{ |R12| |B12| |G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|B1| " + "\n\n";
                assertEquals(expected, player2.getGameState());

            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("R13");
                player3.drawTile("G2");
                player3.drawTile("B13");
                player3.drawTile("O2");
                player3.drawTile("R2");
                player3.drawTile("G13");
                player3.drawTile("G1");

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" + "{ |R11| |R12| |R13| }\n" + "\n" + "==========HAND==========" + "\n" + "|R2| |R13| |B13| |G1| |G2| |G13| |O2| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action (play two melds, then end turn)
                String inString = "1\n" + "R13 B13 G13\n" + "1\n" + "G2 R2 O2\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's hand and table after playing tiles and ending turn
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ *|R13| *|B13| *|G13| }\n" +
                        "{ *|R2| *|G2| *|O2| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                player3.sendUpdatedGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();
    }

    @Test
    @DisplayName("tests for initial 30 points")
    public void initialPoints() throws InterruptedException, IOException {

        /* P1 plays {JH QH KH} */

        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R12");
                player1.drawTile("R13");
                player1.drawTile("R11");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R11| |R12| |R13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|R12| *|R13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());
            }
        });

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        /* P1 plays {QH QC QS} */

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R12");
                player1.drawTile("G12");
                player1.drawTile("B12");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R12| |B12| |G12| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "R12 G12 B12\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R12| *|B12| *|G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        /* P1 plays {9H 10H JH QH KH} */

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R12");
                player1.drawTile("R13");
                player1.drawTile("R9");
                player1.drawTile("R11");
                player1.drawTile("R10");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R9| |R10| |R11| |R12| |R13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "R9 R10 R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R9| *|R10| *|R11| *|R12| *|R13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        /* P1 plays {KH KC KS KD} */

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("B13");
                player1.drawTile("R13");
                player1.drawTile("O13");
                player1.drawTile("G13");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R13| |B13| |G13| |O13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "O13 B13 R13 G13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R13| *|B13| *|G13| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        /* P1 plays {2H 3H 4H} {7S 8S 9S} */

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R2");
                player1.drawTile("R3");
                player1.drawTile("R4");
                player1.drawTile("B7");
                player1.drawTile("B8");
                player1.drawTile("B9");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R2| |R3| |R4| |B7| |B8| |B9| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "R2 R3 R4\n" + "1\n" + "B7 B8 B9\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R2| *|R3| *|R4| }\n" +
                        "{ *|B7| *|B8| *|B9| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        /* P1 plays {2H 2S 2D} {4C 4D 4S 4H} {5D 5S 5H} */

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R2");
                player1.drawTile("B2");
                player1.drawTile("O2");
                player1.drawTile("G4");
                player1.drawTile("O4");
                player1.drawTile("R4");
                player1.drawTile("B4");
                player1.drawTile("O5");
                player1.drawTile("B5");
                player1.drawTile("R5");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R2| |R4| |R5| |B2| |B4| |B5| |G4| |O2| |O4| |O5| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "R2 B2 O2\n" + "1\n" + "B4 R4 G4 O4\n" + "1\n" + "R5 B5 O5\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R2| *|B2| *|O2| }\n" +
                        "{ *|R4| *|B4| *|G4| *|O4| }\n" +
                        "{ *|R5| *|B5| *|O5| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        /* P1 plays {8H 8C 8D} {2H 3H 4H} */

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R8");
                player1.drawTile("G8");
                player1.drawTile("O8");
                player1.drawTile("R2");
                player1.drawTile("R3");
                player1.drawTile("R4");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R2| |R3| |R4| |R8| |G8| |O8| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "R8 G8 O8\n" + "1\n" + "R2 R3 R4\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R8| *|G8| *|O8| }\n" +
                        "{ *|R2| *|R3| *|R4| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        /* P1 plays {2H 2D 2S} {2C 3C 4C} {3H 3S 3D} {5S 6S 7S} */

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R2");
                player1.drawTile("O2");
                player1.drawTile("B2");
                player1.drawTile("G2");
                player1.drawTile("G3");
                player1.drawTile("G4");
                player1.drawTile("R3");
                player1.drawTile("B3");
                player1.drawTile("O3");
                player1.drawTile("B5");
                player1.drawTile("B6");
                player1.drawTile("B7");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R2| |R3| |B2| |B3| |B5| |B6| |B7| |G2| |G3| |G4| |O2| |O3| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "R2 O2 B2\n" + "1\n" + "G2 G3 G4\n" + "1\n" + "R3 B3 O3\n" + "1\n" + "B5 B6 B7\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R2| *|B2| *|O2| }\n" +
                        "{ *|G2| *|G3| *|G4| }\n" +
                        "{ *|R3| *|B3| *|O3| }\n" +
                        "{ *|B5| *|B6| *|B7| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        /* P1 plays {2H 2S 2C 2D} {3C 4C 5C 6C 7C} {4D 5D 6D 7D 8D} and wins! */

        //SERVER THREAD
        GameServer gameServer_endGame = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R2"); player1.drawTile("O2"); player1.drawTile("B2"); player1.drawTile("G2");
                player1.drawTile("G3"); player1.drawTile("G4"); player1.drawTile("G5"); player1.drawTile("G6"); player1.drawTile("G7");
                player1.drawTile("O4"); player1.drawTile("O5"); player1.drawTile("O6"); player1.drawTile("O7"); player1.drawTile("O8");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R2| |B2| |G2| |G3| |G4| |G5| |G6| |G7| |O2| |O4| |O5| |O6| |O7| |O8| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (end turn)
                String inString = "1\n" + "R2 O2 B2 G2\n" + "1\n" + "G3 G4 G5 G6 G7\n" + "1\n" + "O4 O5 O6 O7 O8\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R2| *|B2| *|G2| *|O2| }\n" +
                        "{ *|G3| *|G4| *|G5| *|G6| *|G7| }\n" +
                        "{ *|O4| *|O5| *|O6| *|O7| *|O8| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player1.getGameState());

                //test point value is sufficient (method checks this)
                assertTrue(player1.hasInitialPoints());

                player1.sendUpdatedGame();
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        //test winner
        assertEquals("A", gameServer_endGame.getWinner());

        gameServer_endGame.kill();

    }

    @Test
    @DisplayName("tests playing melds out of hand after initial 30 points")
    public void playMeldsAfterInitialPoints() throws InterruptedException, IOException {

        //setup for 1st turn P1 plays {JH QH KH}, P2 {JS QS KS} and P3 {JD QD KD}
        //start of turn 2: P1 then plays {2C 3C 4C} from hand

        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("R12");
                player1.drawTile("R13");
                player1.drawTile("G2");
                player1.drawTile("G3");
                player1.drawTile("G4");
                player1.drawTile("O1");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R11| |R12| |R13| |G2| |G3| |G4| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|R12| *|R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|G2| |G3| |G4| |O1| " + "\n\n";

                assertEquals(expected, player1.getGameState());
                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G2| |G3| |G4| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (play meld, then end turn)
                inString = "1\n" + "G2 G3 G4\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" +
                        "{ *|G2| *|G3| *|G4| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();
            }});

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("B11");
                player2.drawTile("B12");
                player2.drawTile("B13");
                player2.drawTile("G1");

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|B11| |B12| |B13| |G1| " + "\n\n";

                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "B11 B12 B13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+
                        "{ *|B11| *|B12| *|B13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";

                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");
                player3.drawTile("O1");

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action
                String inString = "1\n" + "O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's view of table and hand after playing tiles
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player3.getGameState());
                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        //setup for 1st turn P1 plays {JH QH KH}, P2 {JS QS KS} and P3 {JD QD KD}
        //start of turn 2: P1 then plays {2C 3C 4C} {8D 9D 10D} from hand

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("R12");
                player1.drawTile("R13");
                player1.drawTile("G2");
                player1.drawTile("G3");
                player1.drawTile("G4");
                player1.drawTile("O8");
                player1.drawTile("O9");
                player1.drawTile("O10");
                player1.drawTile("O13");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R11| |R12| |R13| |G2| |G3| |G4| |O8| |O9| |O10| |O13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|R12| *|R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|G2| |G3| |G4| |O8| |O9| |O10| |O13| " + "\n\n";

                assertEquals(expected, player1.getGameState());
                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G2| |G3| |G4| |O8| |O9| |O10| |O13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (play meld, then end turn)
                inString = "1\n" + "G2 G3 G4\n" + "1\n" + "O8 O9 O10\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" +
                        "{ *|G2| *|G3| *|G4| }\n" +
                        "{ *|O8| *|O9| *|O10| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();
            }});

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("B11");
                player2.drawTile("B12");
                player2.drawTile("B13");
                player2.drawTile("G13");

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|B11| |B12| |B13| |G13| " + "\n\n";

                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "B11 B12 B13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+
                        "{ *|B11| *|B12| *|B13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|G13| " + "\n\n";

                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");
                player3.drawTile("R1");

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action
                String inString = "1\n" + "O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's view of table and hand after playing tiles
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| " + "\n\n";
                assertEquals(expected, player3.getGameState());
                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();
//****************
        //setup for 1st turn P1 plays {JH QH KH}, P2 {JS QS KS} and P3 {JD QD KD}
        //start of turn 2: P1 then plays {2C 2H 2D} from hand

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("R12");
                player1.drawTile("R13");
                player1.drawTile("R2");
                player1.drawTile("G2");
                player1.drawTile("O2");
                player1.drawTile("G1");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R11| |R12| |R13| |G1| |G2| |O2| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|R12| *|R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |G1| |G2| |O2| " + "\n\n";

                assertEquals(expected, player1.getGameState());
                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |G1| |G2| |O2| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (play meld, then end turn)
                inString = "1\n" + "R2 G2 O2\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" +
                        "{ *|R2| *|G2| *|O2| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();
            }});

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("B11");
                player2.drawTile("B12");
                player2.drawTile("B13");
                player2.drawTile("R1");

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |B11| |B12| |B13| " + "\n\n";

                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "B11 B12 B13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+
                        "{ *|B11| *|B12| *|B13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| " + "\n\n";

                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");
                player3.drawTile("B1");

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|B1| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action
                String inString = "1\n" + "O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's view of table and hand after playing tiles
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|B1| " + "\n\n";
                assertEquals(expected, player3.getGameState());
                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        //setup for 1st turn P1 plays {JH QH KH}, P2 {JS QS KS} and P3 {JD QD KD}
        //start of turn 2: P1 then plays {2C 2H 2D} {8D 8H 8S 8C} from hand

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("R12");
                player1.drawTile("R13");
                player1.drawTile("R2");
                player1.drawTile("G2");
                player1.drawTile("O2");
                player1.drawTile("R8");
                player1.drawTile("B8");
                player1.drawTile("O8");
                player1.drawTile("G8");
                player1.drawTile("B1");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R8| |R11| |R12| |R13| |B1| |B8| |G2| |G8| |O2| |O8| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|R12| *|R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R8| |B1| |B8| |G2| |G8| |O2| |O8| " + "\n\n";

                assertEquals(expected, player1.getGameState());
                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R8| |B1| |B8| |G2| |G8| |O2| |O8| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (play meld, then end turn)
                inString = "1\n" + "R2 G2 O2\n" + "1\n" + "R8 B8 G8 O8\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" +
                        "{ *|R2| *|G2| *|O2| }\n" +
                        "{ *|R8| *|B8| *|G8| *|O8| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|B1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();
            }});

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("B11");
                player2.drawTile("B12");
                player2.drawTile("B13");
                player2.drawTile("R1");

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |B11| |B12| |B13| " + "\n\n";

                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "B11 B12 B13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+
                        "{ *|B11| *|B12| *|B13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| " + "\n\n";

                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");
                player3.drawTile("G1");

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action
                String inString = "1\n" + "O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's view of table and hand after playing tiles
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());
                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        //setup for 1st turn P1 plays {JH QH KH}, P2 {JS QS KS} and P3 {JD QD KD}
        //start of turn 2: P1 then plays {2C 2H 2D} {8D 9D 10D} from hand

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("R12");
                player1.drawTile("R13");
                player1.drawTile("R2");
                player1.drawTile("G2");
                player1.drawTile("O2");
                player1.drawTile("O8");
                player1.drawTile("O9");
                player1.drawTile("O10");
                player1.drawTile("B1");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R11| |R12| |R13| |B1| |G2| |O2| |O8| |O9| |O10| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|R12| *|R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |B1| |G2| |O2| |O8| |O9| |O10| " + "\n\n";

                assertEquals(expected, player1.getGameState());
                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |B1| |G2| |O2| |O8| |O9| |O10| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (play meld, then end turn)
                inString = "1\n" + "R2 G2 O2\n" + "1\n" + "O8 O9 O10\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" +
                        "{ *|R2| *|G2| *|O2| }\n" +
                        "{ *|O8| *|O9| *|O10| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|B1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();
            }});

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("B11");
                player2.drawTile("B12");
                player2.drawTile("B13");
                player2.drawTile("O1");

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|B11| |B12| |B13| |O1| " + "\n\n";

                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "B11 B12 B13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+
                        "{ *|B11| *|B12| *|B13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";

                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");
                player3.drawTile("G1");

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action
                String inString = "1\n" + "O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's view of table and hand after playing tiles
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());
                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

//****************
        //setup for 1st turn P1 plays {JH QH KH}, P2 {JS QS KS} and P3 {JD QD KD}
        //start of turn 2: P1 then plays {2C 2H 2D}  {3C 3H 3D} {8C 9C 10C JC QC} from hand

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11"); player1.drawTile("R12"); player1.drawTile("R13");
                player1.drawTile("R2"); player1.drawTile("G2"); player1.drawTile("O2");
                player1.drawTile("G3"); player1.drawTile("R3"); player1.drawTile("O3");
                player1.drawTile("G8"); player1.drawTile("G9"); player1.drawTile("G10"); player1.drawTile("G11"); player1.drawTile("G12");
                player1.drawTile("O13");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R3| |R11| |R12| |R13| |G2| |G3| |G8| |G9| |G10| |G11| |G12| |O2| |O3| |O13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 R12 R13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|R12| *|R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R3| |G2| |G3| |G8| |G9| |G10| |G11| |G12| |O2| |O3| |O13| " + "\n\n";

                assertEquals(expected, player1.getGameState());
                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R3| |G2| |G3| |G8| |G9| |G10| |G11| |G12| |O2| |O3| |O13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (play meld, then end turn)
                inString = "1\n" + "G2 R2 O2\n" + "1\n" + "G3 R3 O3\n" + "1\n" + "G8 G9 G10 G11 G12\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ |O11| |O12| |O13| }\n" +
                        "{ *|R2| *|G2| *|O2| }\n" +
                        "{ *|R3| *|G3| *|O3| }\n" +
                        "{ *|G8| *|G9| *|G10| *|G11| *|G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O13| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();
            }});

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("B11");
                player2.drawTile("B12");
                player2.drawTile("B13");
                player2.drawTile("O1");

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|B11| |B12| |B13| |O1| " + "\n\n";

                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "B11 B12 B13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and hand after play
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n"+
                        "{ *|B11| *|B12| *|B13| }\n"+ "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";

                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");
                player3.drawTile("G1");

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action
                String inString = "1\n" + "O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's view of table and hand after playing tiles
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |R12| |R13| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());
                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();
    }

    @Test
    @DisplayName("tests player having or choosing to draw a tile")
    public void havingChoosingDrawTile() throws InterruptedException, IOException {

        //P1 starts with {2C 2H 2D}  {3C 3H 3D} {8D 9D 10D} {8H 9H 10H} QC 7H in hand and chooses to draw

        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("G2"); player1.drawTile("R2"); player1.drawTile("O2");
                player1.drawTile("G3"); player1.drawTile("R3"); player1.drawTile("O3");
                player1.drawTile("O8"); player1.drawTile("O9"); player1.drawTile("O10");
                player1.drawTile("R8"); player1.drawTile("R9"); player1.drawTile("R10");
                player1.drawTile("G12"); player1.drawTile("R7");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" +
                        "|R2| |R3| |R7| |R8| |R9| |R10| |G2| |G3| |G12| |O2| |O3| |O8| |O9| |O10| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action: play meld, so ending turn without drawing (3) is an option, then optionally draw before ending (2)
                String inString = "1\n" + "R8 R9 R10\n" + "2\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //here, player has choice to draw tile and end turn, or end turn without drawing
                expected = "Select an action: \n" +
                        "(1) Play Meld on Table\n" +
                        "(2) Draw Tile and End Turn\n" +
                        "(3) End Turn\n";
                assertEquals(expected, player1.getOptions());
            }
        });

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        //P1 starts with 2C 2C 2D 3H 3S 3S 5H 6S 7D 9H 10H JC QS KS and has to draw

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("G2"); player1.drawTile("G2"); player1.drawTile("O2");
                player1.drawTile("R3"); player1.drawTile("B3"); player1.drawTile("B3");
                player1.drawTile("R5"); player1.drawTile("B6"); player1.drawTile("O7");
                player1.drawTile("R9"); player1.drawTile("R10"); player1.drawTile("G11");
                player1.drawTile("B12"); player1.drawTile("B13");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" +
                        "|R3| |R5| |R9| |R10| |B3| |B3| |B6| |B12| |B13| |G2| |G2| |G11| |O2| |O7| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "2\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //here, player has no choice but to draw tile and cannot end turn without drawing
                expected = "Select an action: \n" +
                        "(1) Play Meld on Table\n" +
                        "(2) Draw Tile and End Turn\n";
                assertEquals(expected, player1.getOptions());
            }
        });

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();
            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

    }

    @Test
    @DisplayName("tests for declaring winner after all tiles played and reporting scores")
    public void playAllTilesAndScores() throws InterruptedException, IOException {
        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("G1"); player1.drawTile("G2"); player1.drawTile("O2");
                player1.drawTile("R3"); player1.drawTile("B3"); player1.drawTile("B3");
                player1.drawTile("R5"); player1.drawTile("B6"); player1.drawTile("O7");
                player1.drawTile("R9"); player1.drawTile("R10"); player1.drawTile("B11");
                player1.drawTile("B12"); player1.drawTile("B13");

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" +
                        "|R3| |R5| |R9| |R10| |B3| |B3| |B6| |B11| |B12| |B13| |G1| |G2| |O2| |O7| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //simulate draw tile then end turn
                player1.drawTile("R2");

                //prompt p1 for action
                String inString = "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                player1.sendUpdatedGame();
                player1.updateGame();

                //check hand and table
                expected = "==========TABLE==========" + "\n" +
                        "{ |R10| |R11| |R12| |R13| }\n" +
                        "{ |B10| |B11| |B12| |B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |R3| |R5| |R9| |R10| |B3| |B3| |B6| |B11| |B12| |B13| |G1| |G2| |O2| |O7| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                inString = "1\n" + "G2 O2 R2\n" + "1\n" + "B11 B12 B13\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //check hand and table
                expected = "==========TABLE==========" + "\n" +
                        "{ |R10| |R11| |R12| |R13| }\n" +
                        "{ |B10| |B11| |B12| |B13| }\n" +
                        "{ *|R2| *|G2| *|O2| }\n" +
                        "{ *|B11| *|B12| *|B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R3| |R5| |R9| |R10| |B3| |B3| |B6| |G1| |O7| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();
                assertEquals("GAME OVER. Final Scores:\nPlayer 1: -47\nPlayer 2: 0\nPlayer 3: -38\n", player1.getFinalScores());

            }
        });

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand (rig to include tiles that are part of test)
                player2.drawTile("R2"); player2.drawTile("B2"); player2.drawTile("G2");
                player2.drawTile("O2"); player2.drawTile("G3"); player2.drawTile("G4");
                player2.drawTile("G6"); player2.drawTile("G7"); player2.drawTile("O4");
                player2.drawTile("O5"); player2.drawTile("O6"); player2.drawTile("O7");
                player2.drawTile("O8"); player2.drawTile("O9");

                //test p2's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" +
                        "|R2| |B2| |G2| |G3| |G4| |G6| |G7| |O2| |O4| |O5| |O6| |O7| |O8| |O9| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //simulate draw tile then end turn
                player2.drawTile("G5");

                //prompt p2 for action
                String inString = "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                player2.sendUpdatedGame();
                player2.updateGame();

                //check hand and table
                expected = "==========TABLE==========" + "\n" +
                        "{ |R10| |R11| |R12| |R13| }\n" +
                        "{ |B10| |B11| |B12| |B13| }\n" +
                        "{ |R2| |G2| |O2| }\n" +
                        "{ |B11| |B12| |B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R2| |B2| |G2| |G3| |G4| |G5| |G6| |G7| |O2| |O4| |O5| |O6| |O7| |O8| |O9| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action
                inString = "1\n" + "R2 B2 G2 O2\n" + "1\n" + "G3 G4 G5 G6 G7\n" + "1\n" + "O4 O5 O6 O7 O8 O9\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //check hand and table
                expected = "==========TABLE==========" + "\n" +
                        "{ |R10| |R11| |R12| |R13| }\n" +
                        "{ |B10| |B11| |B12| |B13| }\n" +
                        "{ |R2| |G2| |O2| }\n" +
                        "{ |B11| |B12| |B13| }\n" +
                        "{ *|R2| *|B2| *|G2| *|O2| }\n" +
                        "{ *|G3| *|G4| *|G5| *|G6| *|G7| }\n" +
                        "{ *|O4| *|O5| *|O6| *|O7| *|O8| *|O9| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "" + "\n\n";
                assertEquals(expected, player2.getGameState());

                player2.sendUpdatedGame();
                assertEquals("GAME OVER. Final Scores:\nPlayer 1: -47\nPlayer 2: 0\nPlayer 3: -38\n", player2.getFinalScores());
            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand (rig to include tiles that are part of test)
                player3.drawTile("R4"); player3.drawTile("O6"); player3.drawTile("B6");
                player3.drawTile("B7"); player3.drawTile("R7"); player3.drawTile("G8");
                player3.drawTile("R10"); player3.drawTile("R11"); player3.drawTile("R12");
                player3.drawTile("R13"); player3.drawTile("B10"); player3.drawTile("B11");
                player3.drawTile("B12"); player3.drawTile("B13");

                //test p3's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" +
                        "|R4| |R7| |R10| |R11| |R12| |R13| |B6| |B7| |B10| |B11| |B12| |B13| |G8| |O6| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action
                String inString = "1\n" + "R10 R11 R12 R13\n" + "1\n" + "B10 B11 B12 B13\n" +"3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //check hand and table after play
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R10| *|R11| *|R12| *|R13| }\n" +
                        "{ *|B10| *|B11| *|B12| *|B13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R4| |R7| |B6| |B7| |G8| |O6| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                player3.sendUpdatedGame();
                player3.updateGame();
                assertEquals("GAME OVER. Final Scores:\nPlayer 1: -47\nPlayer 2: 0\nPlayer 3: -38\n", player3.getFinalScores());
            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        assertEquals("B", gameServer.getWinner());

        gameServer.kill();
    }

    @Test
    @DisplayName("completing a partial set from a hand by reusing from a set of 4 of the table")
    public void partialHandSetReuseTableSet() throws InterruptedException, IOException {

        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("O11");
                player1.drawTile("B11");
                player1.drawTile("G11");

                player1.drawTile("O1");
                //more tiles, which don't matter...

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R11| |B11| |G11| |O1| |O11| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 O11 B11 G11\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|B11| *|G11| *|O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (draw and end)
                inString = "2\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                player1.sendUpdatedGame();
            }});

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("R12");
                player2.drawTile("B12");
                player2.drawTile("G12");

                player2.drawTile("B11");
                player2.drawTile("G11");

                player2.drawTile("R1");
                //more tiles, which don't matter...

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |R12| |B11| |B12| |G11| |G12| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "R12 B12 G12\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ *|R12| *|B12| *|G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |B11| |G11| " + "\n\n";
                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();

                player2.updateGame();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |B11| |G11| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action
                inString = "1\n" + "1:R11 B11 G11\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" +
                        "{ !|R11| *|B11| *|G11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                player2.sendUpdatedGame();

            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O7");
                player3.drawTile("O8");
                player3.drawTile("O9");
                player3.drawTile("O10");
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");

                player3.drawTile("G1");
                //more tiles, which don't matter...

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| |O7| |O8| |O9| |O10| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action (play two melds, then end turn)
                String inString = "1\n" + "O7 O8 O9 O10 O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's hand and table after playing tiles and ending turn
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ *|O7| *|O8| *|O9| *|O10| *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();
    }

    @Test
    @DisplayName("completing a partial run from a hand by reusing from a set of 4 of the table")
    public void partialHandRunReuseTableSet() throws InterruptedException, IOException {

        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("O11");
                player1.drawTile("B11");
                player1.drawTile("G11");

                player1.drawTile("O1");
                //more tiles, which don't matter...

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R11| |B11| |G11| |O1| |O11| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 O11 B11 G11\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|B11| *|G11| *|O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action (draw and end)
                inString = "2\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                player1.sendUpdatedGame();
            }});

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("R12");
                player2.drawTile("B12");
                player2.drawTile("G12");

                player2.drawTile("R12");
                player2.drawTile("R13");

                player2.drawTile("R1");
                //more tiles, which don't matter...

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |R12| |R12| |R13| |B12| |G12| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "R12 B12 G12\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ *|R12| *|B12| *|G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |R12| |R13| " + "\n\n";
                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();

                player2.updateGame();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |R12| |R13| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action
                inString = "1\n" + "1:R11 R12 R13\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" +
                        "{ !|R11| *|R12| *|R13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                player2.sendUpdatedGame();

            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O7");
                player3.drawTile("O8");
                player3.drawTile("O9");
                player3.drawTile("O10");
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");

                player3.drawTile("G1");
                //more tiles, which don't matter...

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| |O7| |O8| |O9| |O10| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action (play two melds, then end turn)
                String inString = "1\n" + "O7 O8 O9 O10 O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's hand and table after playing tiles and ending turn
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ *|O7| *|O8| *|O9| *|O10| *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();
    }

    @Test
    @DisplayName("completing a partial set from a hand by reusing from a run of the table")
    public void partialHandSetReuseTableRun() throws InterruptedException, IOException {

        //reusing 7D from table

        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("O11");
                player1.drawTile("B11");
                player1.drawTile("G11");

                player1.drawTile("B7");
                player1.drawTile("R7");

                player1.drawTile("O1");
                //more tiles, which don't matter...

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R7| |R11| |B7| |B11| |G11| |O1| |O11| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 O11 B11 G11\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|B11| *|G11| *|O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R7| |B7| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R7| |B7| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                inString = "1\n" + "3:O7 B7 R7\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O8| |O9| |O10| |O11| |O12| |O13| }\n" +
                        "{ *|R7| *|B7| !|O7| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
            }});

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("R12");
                player2.drawTile("B12");
                player2.drawTile("G12");

                player2.drawTile("R1");
                //more tiles, which don't matter...

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |R12| |B12| |G12| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "R12 B12 G12\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ *|R12| *|B12| *|G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| " + "\n\n";
                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O7");
                player3.drawTile("O8");
                player3.drawTile("O9");
                player3.drawTile("O10");
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");

                player3.drawTile("G1");
                //more tiles, which don't matter...

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| |O7| |O8| |O9| |O10| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action (play two melds, then end turn)
                String inString = "1\n" + "O7 O8 O9 O10 O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's hand and table after playing tiles and ending turn
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ *|O7| *|O8| *|O9| *|O10| *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();

        //reusing KD from table

        //SERVER THREAD
        gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("O11");
                player1.drawTile("B11");
                player1.drawTile("G11");

                player1.drawTile("R13");
                player1.drawTile("B13");

                player1.drawTile("O1");
                //more tiles, which don't matter...

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R11| |R13| |B11| |B13| |G11| |O1| |O11| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 O11 B11 G11\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|B11| *|G11| *|O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R13| |B13| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R13| |B13| |O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                inString = "1\n" + "R13 B13 3:O13\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| }\n" +
                        "{ *|R13| *|B13| !|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
            }});

        //PLAYER 2 THREAD
        t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("R12");
                player2.drawTile("B12");
                player2.drawTile("G12");

                player2.drawTile("R1");
                //more tiles, which don't matter...

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |R12| |B12| |G12| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "R12 B12 G12\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ *|R12| *|B12| *|G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| " + "\n\n";
                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O7");
                player3.drawTile("O8");
                player3.drawTile("O9");
                player3.drawTile("O10");
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");

                player3.drawTile("G1");
                //more tiles, which don't matter...

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| |O7| |O8| |O9| |O10| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action (play two melds, then end turn)
                String inString = "1\n" + "O7 O8 O9 O10 O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's hand and table after playing tiles and ending turn
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ *|O7| *|O8| *|O9| *|O10| *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();
    }

    @Test
    @DisplayName("completing a partial run from a hand by reusing from run of the table")
    public void partialHandRunReuseTableRun() throws InterruptedException, IOException {

        //SERVER THREAD
        GameServer gameServer = Config.startTestServer();

        //PLAYER 1 THREAD
        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                Player player1 = new Player("A");
                player1.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player1.updateGame();

                //draw p1's initial hand (rig to include tiles that are part of test)
                player1.drawTile("R11");
                player1.drawTile("O11");
                player1.drawTile("B11");
                player1.drawTile("G11");

                player1.drawTile("O8");
                player1.drawTile("O9");

                player1.drawTile("O1");
                //more tiles, which don't matter...

                //test p1's view of table (empty) and their hand
                String expected = "==========TABLE==========" + "\n" + "\n" + "==========HAND==========" + "\n" + "|R11| |B11| |G11| |O1| |O8| |O9| |O11| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                String inString = "1\n" + "R11 O11 B11 G11\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ *|R11| *|B11| *|G11| *|O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| |O8| |O9| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
                player1.updateGame();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O7| |O8| |O9| |O10| |O11| |O12| |O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| |O8| |O9| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                //prompt p1 for action
                inString = "1\n" + "O8 O9 3:O10\n" + "3\n";
                in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player1.getAction();

                //test p1's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ |O11| |O12| |O13| }\n" +
                        "{ |O7| |O8| |O9| }\n" +
                        "{ *|O8| *|O9| !|O10| }\n" +  "\n" +
                        "==========HAND==========" + "\n" +
                        "|O1| " + "\n\n";
                assertEquals(expected, player1.getGameState());

                player1.sendUpdatedGame();
            }});

        //PLAYER 2 THREAD
        Thread t2 = new Thread(new Runnable() {
            public void run()
            {
                Player player2 = new Player("B");
                player2.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player2.updateGame();

                //draw p2's initial hand
                player2.drawTile("R12");
                player2.drawTile("B12");
                player2.drawTile("G12");

                player2.drawTile("R1");
                //more tiles, which don't matter...

                //test p2's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| |R12| |B12| |G12| " + "\n\n";
                assertEquals(expected, player2.getGameState());

                //prompt p2 for action (play meld, then end turn)
                String inString = "1\n" + "R12 B12 G12\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player2.getAction();

                //test p2's view of table and their hand
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ *|R12| *|B12| *|G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|R1| " + "\n\n";
                assertEquals(expected, player2.getGameState());
                player2.sendUpdatedGame();
                player2.updateGame();

            }});

        //PLAYER 3 THREAD
        Thread t3 = new Thread(new Runnable() {
            public void run()
            {
                Player player3 = new Player("C");
                player3.connectToClient(Config.GAME_SERVER_PORT_NUMBER);
                player3.updateGame();

                //draw p3's initial hand
                player3.drawTile("O7");
                player3.drawTile("O8");
                player3.drawTile("O9");
                player3.drawTile("O10");
                player3.drawTile("O11");
                player3.drawTile("O12");
                player3.drawTile("O13");

                player3.drawTile("G1");
                //more tiles, which don't matter...

                //test p3's view of table and their hand
                String expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| |O7| |O8| |O9| |O10| |O11| |O12| |O13| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                //prompt p3 for action (play two melds, then end turn)
                String inString = "1\n" + "O7 O8 O9 O10 O11 O12 O13\n" + "3\n";
                ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
                System.setIn(in);
                player3.getAction();

                //test p3's hand and table after playing tiles and ending turn
                expected = "==========TABLE==========" + "\n" +
                        "{ |R11| |B11| |G11| |O11| }\n" +
                        "{ |R12| |B12| |G12| }\n" +
                        "{ *|O7| *|O8| *|O9| *|O10| *|O11| *|O12| *|O13| }\n" + "\n" +
                        "==========HAND==========" + "\n" +
                        "|G1| " + "\n\n";
                assertEquals(expected, player3.getGameState());

                player3.sendUpdatedGame();
                player3.updateGame();

            }});

        //start threads with slight delay in between to ensure proper order
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t3.start();
        TimeUnit.SECONDS.sleep(1);

        gameServer.kill();
    }
}
