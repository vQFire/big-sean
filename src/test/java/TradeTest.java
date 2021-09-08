import Application.Firebase;
import Models.Game;
import Controllers.TradeController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Models.Player;
import javafx.scene.input.MouseEvent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Ion Middelraad
 */
public class TradeTest extends AbstractTest{
    TradeController tradeController;
    Map<String, ArrayList<String>> tradeStatus;
    Game game;


    @Before
    public void setUpTest() {
        Firebase.initialiseFirebase();

        tradeStatus = new HashMap<>();
        tradeStatus.put("wants", new ArrayList<String>());
        tradeStatus.get("wants").add("grain");
        tradeStatus.put("gives", new ArrayList<String>());
        tradeStatus.get("gives").add("brick");
        tradeStatus.get("gives").add("brick");
        tradeStatus.put("accepted", new ArrayList<String>());
        tradeStatus.put("refused", new ArrayList<String>());
        tradeStatus.put("tradeSender", new ArrayList<String>());
        tradeStatus.get("tradeSender").add("0");

        game = new Game();
        game.setCode("Le_TestGame");
        game.addPlayer(new Player("player1"));
        game.addPlayer(new Player("player2"));
        game.getPlayers().get(0).getInventory().addResource("brick", 2);
        game.getPlayers().get(1).getInventory().addResource("grain", 1);

    }
    @Test
    public void Should_SwapResourcesWithOtherPlayer_When_TradeAccepted() {
        tradeController = new TradeController(game.getCode(),game.getCurrentPlayer(),game);

        tradeController.updatePlayerInventory(tradeStatus.get("gives"), tradeStatus.get("wants"), 1, tradeStatus);
        int amountOfRequestedPlayer = game.getPlayers().get(0).getInventory().getResourceCards().get("grain");
        int amountOfAcceptedPlayer = game.getPlayers().get(1).getInventory().getResourceCards().get("brick");
        assertEquals(1, amountOfRequestedPlayer);
        assertEquals(2, amountOfAcceptedPlayer);
    }
}
