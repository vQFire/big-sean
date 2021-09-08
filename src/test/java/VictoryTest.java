import Application.SceneManager;
import Controllers.GameController;
import Controllers.LocationsController;
import Controllers.VictoryController;
import Models.*;
import javafx.fxml.FXMLLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Sean Vis
 */
public class VictoryTest extends AbstractTest {
    private VictoryController victoryController;

    @Before
    public void setupGameController() {
        try {
            FXMLLoader loader = SceneManager.getSceneLoader("VictoryView");
            loader.load();

            victoryController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Should_ReturnWinMessage() {
        victoryController.setMessage("sien");
        String result = victoryController.getWinMessage().getText();

        assertEquals("sien has won the game!", result);
    }

}
