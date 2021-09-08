package Controllers;

import Application.Firebase;
import Models.Chat;
import Models.Game;
import Models.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the MonopolyView.fxml
 * Contains all the functions for using the monopolyCard
 *
 * @author Tijs Groenendaal
 */
public class MonopolyController {
    private String chosenCardType;
    private Game game;
    private int localPlayerIndex;

    public Button ore, grain, brick, lumber, wool;
    public Button useButton;
    public Label card;

    /**
     * Executes when player presses monopoly card button on MonopolyView
     * Updates all the players their inventory in Firebase
     *
     * @author Tijs Groenendaal
     */
    public void useMonopolyCard() {
        if (chosenCardType != null) {
            transferResources();

            Firebase.db.collection("games").document(game.getCode()).set(game);

            Stage stage = (Stage) useButton.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Transfers the resources from the inventory's
     *
     * @author Tijs Groenendaal
     */
    public void transferResources() {
        int receiveAmount = 0;

        for (Player player : game.getPlayers()) {
            receiveAmount += player.getInventory().getResourceCards().get(chosenCardType);
            player.getInventory().getResourceCards().put(chosenCardType, 0);
        }

        game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().put(chosenCardType, + receiveAmount);

        int prev = game.getPlayers().get(localPlayerIndex).getInventory().getDevelopmentCards().get("monopoly");
        game.getPlayers().get(localPlayerIndex).getInventory().getDevelopmentCards().put("monopoly", prev - 1);
    }


    /**
     * Sets the card chosen by the player in a variable
     *
     * @author Tijs Groenendaal
     * @param actionEvent Contains the source of the Event
     */
    public void chooseCard(ActionEvent actionEvent) {
        Button chosenCard = (Button) actionEvent.getSource();
        chosenCardType = chosenCard.getId();

        card.setText(chosenCardType);
    }

    /**
     * Is called in {@link GameController#playMonopolyCard()}
     *
     * @author Tijs Groenendaal
     */
    public void setGame(Game game, int localPlayerIndex) {
        this.localPlayerIndex = localPlayerIndex;
        this.game = game;
    }

    public String getChosenCardType() {
        return chosenCardType;
    }

    public void setChosenCardType(String chosenCardType) {
        this.chosenCardType = chosenCardType;
    }

    public Game getGame() {
        return game;
    }
}
