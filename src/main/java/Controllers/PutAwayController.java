package Controllers;

import Application.Firebase;
import Models.Game;
import Models.Inventory;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The controller of the PutAwayView.fxml
 * Contains all the functions for discarding your cards when 7 is thrown
 *
 * @author Tijs Groenendaal
 */
public class PutAwayController implements Initializable{
    private Game game;
    private int localPlayerIndex;
    // Amount of cards the player has to dispose
    private int cardsToPut;
    // Amount of cards the player has chosen
    private int size;

    public boolean done = false;

    // Map used the store the amount that need to be removed, Also used to display on screen
    private Map<String, Integer> toRemove = new HashMap<>();
    // Map used to store the Label where the values are displayed on screen
    private Map<String, Label> typeAmount = new HashMap<>();
    // Map used to store the old inventory of the player
    private Map<String, Integer> oldTypeAmount = new HashMap<>();

    public Label lumber_amount, wool_amount, brick_amount, grain_amount, ore_amount;
    public Button confirm;

    /**
     * Is called in {@link GameController#startPortTrade(MouseEvent)} when player clicks on a Port.
     * Gives the local game to PutAwayController so inventory can be changed
     *
     * @author Tijs Groenendaal
     */
    public void setGame(Game game, int localPlayerIndex) {
        this.game = game;
        this.localPlayerIndex = localPlayerIndex;
        Inventory inventory = game.getPlayers().get(localPlayerIndex).getInventory();

        oldTypeAmount.put("lumber", inventory.getResourceCards().get("lumber"));
        oldTypeAmount.put("wool", inventory.getResourceCards().get("wool"));
        oldTypeAmount.put("brick", inventory.getResourceCards().get("brick"));
        oldTypeAmount.put("ore", inventory.getResourceCards().get("ore"));
        oldTypeAmount.put("grain", inventory.getResourceCards().get("grain"));
    }

    /**
     * Adds a card to the toUpdate.
     *
     * @author Tijs Groenendaal
     *
     * @param actionEvent Contains the source of the Event.
     */
    public void addCard(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String type = button.getId().substring(0, button.getId().length() - 1);

        if (oldTypeAmount.get(type) > toRemove.get(type)) {
            if (cardsToPut > size) {
                int prev = toRemove.get(type);
                toRemove.put(type, prev + 1);

                //Set the text on the View
                typeAmount.get(type).setText(toRemove.get(type).toString());
                size ++;

                //Sets the confirm button on screen to active
                if (size == cardsToPut) {
                    confirm.setDisable(false);
                }
            }
        }
    }

    /**
     * Removes the card from the toUpdate.
     *
     * @author Tijs Groenendaal
     *
     * @param actionEvent Contains the source of the Event.
     */
    public void removeCard(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String type = button.getId().substring(0, button.getId().length() - 1);

        System.out.println(oldTypeAmount.get(type));
        System.out.println(toRemove.get(type));
        if (oldTypeAmount.get(type) >= toRemove.get(type) && toRemove.get(type) > 0) {
            if (size > 0) {
                int prev = toRemove.get(type);
                toRemove.put(type, prev - 1);

                //Set the text on the View
                typeAmount.get(type).setText(toRemove.get(type).toString());
                size --;

                //Sets the confirm button on screen to disable
                confirm.setDisable(true);
            }
        }
    }

    /**
     * Is called when the player clicks Confirm in the View.Fxml
     *
     * @author Tijs Groenendaal
     */
    public void confirmPutAway() {
        removeAddResourceFromInventory(toRemove);

        Firebase.db.collection("games").document(game.getCode()).set(game);
        Stage stage = (Stage) lumber_amount.getScene().getWindow();
        stage.close();
    }

    /**
     * Removes all the chosen cards from the players inventory.
     *
     * @author Tijs Groenendaal
     *
     * @param toUpdate an Map that contains all the amounts of the cards that need to be removed.
     */
    private void removeAddResourceFromInventory(Map<String, Integer> toUpdate) {
        for (Map.Entry<String, Integer> entry : toUpdate.entrySet()) {
            game.getPlayers().get(localPlayerIndex).getInventory().addResource(entry.getKey(), entry.getValue() * -1);
        }
    }

    public void setAmount(int cardsToPut) {
        this.cardsToPut = cardsToPut;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeAmount.put("wool", wool_amount);
        typeAmount.put("lumber", lumber_amount);
        typeAmount.put("brick", brick_amount);
        typeAmount.put("grain", grain_amount);
        typeAmount.put("ore", ore_amount);

        toRemove.put("wool", 0);
        toRemove.put("lumber", 0);
        toRemove.put("brick", 0);
        toRemove.put("grain", 0);
        toRemove.put("ore", 0);

//        Stage stage = (Stage) lumber_amount.getScene().getWindow();
//        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent windowEvent) {
//                if (done) {
//                    windowEvent.consume();
//                } else {
//                    Alert alert = new Alert(Alert.AlertType.WARNING, "You first have to discard some cards");
//                    alert.showAndWait();
//                }
//            }
//        });
    }
}
