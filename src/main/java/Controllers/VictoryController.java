package Controllers;

import Application.SceneManager;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Sean Vis
 */
public class VictoryController implements Initializable {
    public Label winMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Sets the Label winMessage to the correct format with playername.
     *
     * @author Sean Vis
     *
     * @param player String containing the name of the player
     */
    public void setMessage(String player) {
        winMessage.setText(player + " has won the game!");
    }

    public Label getWinMessage() {
        return winMessage;
    }

    /**
     * Exits the application.
     *
     * @author Sean Vis
     */
    public void exitApplication() {
        Platform.exit();
    }

    public void returnToMainMenu() {
        SceneManager.setShowingScene("Main Menu");
    }

}
