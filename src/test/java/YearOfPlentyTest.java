import Application.Firebase;
import Application.SceneManager;
import Controllers.YearOfPlentyController;
import Models.Game;
import Models.Player;
import javafx.fxml.FXMLLoader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class YearOfPlentyTest extends AbstractTest {
    YearOfPlentyController yearOfPlentyController;

    @Ignore
    private Game setupGame() {
        Game game = new Game();
        game.setCode("TESTGAME");
        Player player = new Player("Piet Paulusma");
        player.setHost(true);

        game.getPlayers().add(player);
        return game;
    }

    @Before
    public void setupYearOfPlentyController() {
        try {
            FXMLLoader loader = SceneManager.getSceneLoader("YearOfPlentyView");
            loader.load();
            yearOfPlentyController = loader.getController();
            yearOfPlentyController.setGame(setupGame(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_DoNothing_When_PlayerHasNotClickedResources() {
        yearOfPlentyController.setCard1("wool");
        assertFalse(yearOfPlentyController.useYearOfPlenty());
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_UpdateInventory_When_PlayerHasChosenResources_InYearOfPlenty() {
        yearOfPlentyController.setCard1("wool");
        yearOfPlentyController.setCard2("lumber");
        yearOfPlentyController.transferCards();
        assertEquals(1, (int) yearOfPlentyController.getGame().getPlayers().get(yearOfPlentyController.getLocalPlayerIndex()).getInventory().getResourceCards().get("wool"));
        assertEquals(1, (int) yearOfPlentyController.getGame().getPlayers().get(yearOfPlentyController.getLocalPlayerIndex()).getInventory().getResourceCards().get("lumber"));
    }
}
