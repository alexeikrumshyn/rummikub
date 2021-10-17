package rummikub;

import io.cucumber.java.en.*;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefMeldTesting {

    LocalTestServer srv;
    TileCollection testCollection;

    @Given("Test Server is started")
    public void test_server_is_started() {
        srv = new LocalTestServer();
    }

    @When("Player {int} plays {string}")
    public void player_plays(int pNum, String tiles) {
        String[] tilesStr = tiles.split(" ");
        for (String str: tilesStr) {
            srv.players[pNum-1].drawTile(str);
        }
        String inString = "1\n" + tiles + "\n" + "3\n";
        ByteArrayInputStream in = new ByteArrayInputStream((inString).getBytes());
        System.setIn(in);
        srv.players[pNum-1].getAction();
    }

    @Then("table contains {string}")
    public void table_contains(String expected) {
        assertEquals(expected, srv.game.getTable().replace("|", ""));
    }

    @Then("Player {int} hand contains {string}")
    public void player_hand_contains(int pNum, String expected) {

        String actual = srv.players[pNum-1].getHand();

        if (expected.equals("")) {
            assertEquals(expected,actual);
            return;
        }

        String[] expectedTiles = expected.substring(0,expected.length()-1).split(" ");
        String actualHand = actual.substring(0,actual.length()-1).replace("|", "");

        System.out.println(actualHand);
        assertEquals(expectedTiles.length, actualHand.split(" ").length);
        for (int i = 0; i < expectedTiles.length; ++i) {
            if (!expectedTiles[i].equals("?"))
                assertTrue(actualHand.contains(expectedTiles[i]));
        }
    }
}
