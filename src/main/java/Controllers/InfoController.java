package Controllers;

import Application.SceneManager;
import javafx.event.ActionEvent;

public class InfoController {
    /**
     * Returns to previous scene.
     * Activated on fxml button press.
     * @param actionEvent
     */
    public void returnTo(ActionEvent actionEvent) {
        SceneManager.backToLastScene();
    }
}
