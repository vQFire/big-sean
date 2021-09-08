package Exceptions;

import javafx.scene.control.Alert;

/**
 * @author Sean Vis
 */
public class NotEnoughResourcesToSteal extends Exception{
    public NotEnoughResourcesToSteal() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The player does not have enough resources to steal from.");
        alert.show();
    }
}
