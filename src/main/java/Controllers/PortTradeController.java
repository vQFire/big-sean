package Controllers;

import Application.Firebase;
import Exceptions.NotEnoughResourcesException;
import Models.Game;
import Models.Port;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * The controller of the PortTradeView.fxml
 * Contains all the functions for trading a with the Port
 *
 * @author Tijs Groenendaal
 * @author Ion Middelraad
 */
public class PortTradeController {
    public Label give, receive, tradeForNumber;
    public Button ore, grain, wool, brick, lumber, trade_lumber, trade_wool, trade_brick, trade_grain, trade_ore;
    public Button confirmButton;

    private Port port;
    private Game game;
    private int localPlayerIndex;
    private String giveType;
    private String receiveType;

    Map<String, Button> getButtons;
    Map<String, Button> giveButtons;

    /**
     * Is called in {@link GameController#startPortTrade(MouseEvent)}
     *
     * @author Tijs Groenendaal
     *
     * @param port the port that is clicked. Is retrieved in the GameController
     * @param game the GameModel. contains all the inventory's
     */
    public void setup(Port port, Game game, int localPlayerIndex ) {
        this.port = port;
        this.game = game;
        this.localPlayerIndex = localPlayerIndex;
        giveButtons = new HashMap<>() {{
            put("ore", ore);
            put("grain", grain);
            put("wool", wool);
            put("brick", brick);
            put("lumber", lumber);
        }};
        getButtons = new HashMap<>() {{
            put("lumber", trade_lumber);
            put("brick", trade_brick);
            put("grain", trade_grain);
            put("wool", trade_wool);
            put("ore", trade_ore);
        }};

        tradeForNumber.setText(String.format("Trade %s", port.giveAmount()));

        if (port.getReceive() == null) {
            for (Map.Entry<String, Button> entry : giveButtons.entrySet()) {
                entry.getValue().setVisible(true);
            }
        } else {
            for (Map.Entry<String, Button> entry : giveButtons.entrySet()) {
                if (entry.getKey().equals(port.getReceive())) {
                    entry.getValue().setVisible(true);
                } else {
                    entry.getValue().setVisible(false);
                }
            }
        }
    }

    /**
     * Sets the card that is clicked to GiveCard.
     *
     * @author Tijs Groenendaal
     *
     * @param actionEvent Contains the source of Event
     * @throws NotEnoughResourcesException when the player doesn't have this card
     */
    public void selectGiveCard(ActionEvent actionEvent) throws NotEnoughResourcesException {
        Button button = (Button) actionEvent.getSource();
        String type = button.getId();

        if (port.giveAmount() <= game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().get(type)) {
            giveType = type;
            give.setText(String.format("%s", type));

            confirmButton.setDisable(receiveType == null);
        } else {
            throw new NotEnoughResourcesException();
        }
    }

    /**
     * Sets the card that is clicked to ReceiveCard
     *
     * @author Tijs Groenendaal
     *
     * @param actionEvent Contains the source of the Event
     */
    public void selectReceiveCard(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String type = button.getId().substring(6);

        receive.setText(type);
        receiveType = type;

        confirmButton.setDisable(giveType == null);
    }

    /**
     * Is called when the player clicks on Confirm in View.fxml
     *
     * @author Tijs Groenendaal
     */
    public void confirmTrade() {
        int prevRec = game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().get(receiveType) ;
        game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().put(receiveType, prevRec + 1);

        int prevGive = game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().get(giveType);
        game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().put(giveType, prevGive - port.giveAmount());

        Firebase.db.collection("games").document(game.getCode()).set(game);

        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}
