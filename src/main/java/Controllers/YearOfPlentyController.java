package Controllers;

import Application.Firebase;
import Models.Game;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * The controller of the YearOfPlentyView.fxml
 * Contains all the functions for using the year of plenty card.
 *
 * @author Tijs Groenendaal
 */
public class YearOfPlentyController {
    public Button ore1, lumber1, brick1, wool1, grain1;
    public Button ore2, lumber2, brick2, wool2, grain2;
    public Label cardSlot1, cardSlot2;
    private String card1, card2;
    private Game game;
    private int localPlayerIndex;

    /**
     * Is called when player clicks the Card button
     */
    public boolean useYearOfPlenty() {
        if (card1 != null && card2 != null) {
            transferCards();
            Firebase.db.collection("games").document(game.getCode()).set(game);
            Stage stage = (Stage) ore1.getScene().getWindow();
            stage.close();
            return true;
        } else {
            return false;
        }
    }

    public void transferCards() {
        int prevCard1 = game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().get(card1);
        game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().put(card1, prevCard1 + 1);

        int prevCard2 = game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().get(card2);
        game.getPlayers().get(localPlayerIndex).getInventory().getResourceCards().put(card2, prevCard2 + 1);

        int prevDev = game.getPlayers().get(localPlayerIndex).getInventory().getDevelopmentCards().get("year");
        game.getPlayers().get(localPlayerIndex).getInventory().getDevelopmentCards().put("year", prevDev - 1);
    }

    /**
     *
     * @param actionEvent Contains the source of the Event
     */
    public void addSlot1(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String type = button.getId().substring(0, button.getId().length() - 1);

        card1 = type;
        cardSlot1.setText(type);
    }

    /**
     *
     * @param actionEvent Contains the source of the Event
     */
    public void addSlot2(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String type = button.getId().substring(0, button.getId().length() - 1);

        card2 = type;
        cardSlot2.setText(type);
    }

    /**
     *
     * @param game
     */
    public void setGame(Game game, int localPlayerIndex) {
        this.game = game;
        this.localPlayerIndex = localPlayerIndex;
    }

    public Game getGame() {
        return game;
    }

    public String getCard1() {
        return card1;
    }

    public String getCard2() {
        return card2;
    }

    public void setCard1(String card1) {
        this.card1 = card1;
    }

    public void setCard2(String card2) {
        this.card2 = card2;
    }

    public int getLocalPlayerIndex() {
        return localPlayerIndex;
    }

    public void setLocalPlayerIndex(int localPlayerIndex) {
        this.localPlayerIndex = localPlayerIndex;
    }
}
