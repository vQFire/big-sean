package Exceptions;

import javafx.scene.control.Alert;

public class NotEnoughResourcesException extends Exception {
    public NotEnoughResourcesException () {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "You don't have enough resources");
        alert.show();
    }
}
