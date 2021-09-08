import Application.Firebase;
import Controllers.MonopolyController;
import Models.Game;
import Models.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MonopolyTest {
    MonopolyController monopolyController;
    Game game;

    @Before
    public void setupTest() {
        Firebase.initialiseFirebase();

        game = new Game();
        game.setCode("TESTGAME");
        game.addPlayer(new Player("1"));
        game.addPlayer(new Player("2"));
        game.addPlayer(new Player("3"));
        game.addPlayer(new Player("4"));

        game.getPlayers().get(1).getInventory().addResource("wool", 1);
        game.getPlayers().get(2).getInventory().addResource("wool", 1);
        game.getPlayers().get(3).getInventory().addResource("wool", 1);

        monopolyController = new MonopolyController();
        monopolyController.setGame(game, 0);
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_DoNothing_When_NoCardIsSelected() {
        monopolyController.useMonopolyCard();
        assertEquals(0, monopolyController.getGame().getPlayers().get(0).getInventory().totalResources());
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_GiveResourcesOfAllPlayer_When_PlayerClicksConfirm() {
        monopolyController.setChosenCardType("wool");
        monopolyController.transferResources();

        assertEquals(3, monopolyController.getGame().getPlayers().get(0).getInventory().totalResources());
    }
}
