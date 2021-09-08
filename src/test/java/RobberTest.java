import Application.SceneManager;
import Controllers.GameController;
import Controllers.LocationsController;
import Models.*;
import javafx.fxml.FXMLLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Sean Vis
 */
public class RobberTest extends AbstractTest{
    private GameController gameController;
    private LocationsController locationsController;

    @Before
    public void setupGameController () {
        try {
            FXMLLoader loader = SceneManager.getSceneLoader("GameView");
            loader.load();

            gameController = loader.getController();
            locationsController = new LocationsController(gameController.buildingLocations, gameController.roadLocations, gameController.robberLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Game setupGame() {
        Game game = new Game();
        Chat chat = new Chat(game.getCode());
        Player player = new Player("Piet Paulusma");
        player.setHost(true);

        game.getPlayers().add(player);

        gameController.setupGame(chat, game, player);
        return game;
    }

    @Test
    public void Should_HaveDefaultAmountOfLocations_When_SettingUpGame () {
        int totalLocations = locationsController.getRobberLocations().size();
        assertEquals(19, totalLocations);
    }

    @Test
    public void Should_HaveOneTileWithRobber_When_SettingUpGame() {
        Game game = setupGame();
        boolean hasRobber = false;
        for (Tile tile: gameController.getTiles().getTiles()) {
            if (tile.getHasRobber()) {
                if (!hasRobber) {
                    hasRobber = true;
                } else if (hasRobber) {
                    hasRobber = false;
                }
            }
        }
        assertTrue(hasRobber);
    }
}
