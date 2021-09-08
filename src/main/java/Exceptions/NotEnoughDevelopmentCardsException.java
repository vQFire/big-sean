package Exceptions;

import javafx.scene.control.Alert;

public class NotEnoughDevelopmentCardsException extends Exception {
    public NotEnoughDevelopmentCardsException () {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "You don't have enough development cards");
        alert.show();
    }
}
