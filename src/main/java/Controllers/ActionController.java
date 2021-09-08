package Controllers;

import Application.Firebase;
import Application.SceneManager;
import Exceptions.NotEnoughDevelopmentCardsException;
import Exceptions.NotEnoughResourcesException;
import Models.*;
import com.google.cloud.firestore.WriteBatch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ActionController extends AbstractController {
    private LocationsController locationsController;
    private Chat chat;
    private Port[] ports;
    public boolean usingRoadCard = false;

    public ActionController () {}

    public ActionController (LocationsController locationsController, Chat chat, Port[] ports) {
        this.chat = chat;
        this.locationsController = locationsController;
        this.ports = ports;
    }

    /**
     * Creates two random integers between 1 and 6
     *
     * @return new instance of Dice
     * @author Ion Middelraad
     */
    public Dice rollDice() {
        Random rand = new Random();
        int number1 = rand.nextInt(6) + 1;
        int number2 = rand.nextInt(6) + 1;

        return new Dice(number1, number2);
    }

    /**
     * When it is the player's turn and the dice hasn't been thrown yet it generates a new dice.
     *
     * @author Ion Middelraad
     */
    public void throwDice() {
        Dice dice = rollDice();

        dice.turn = game.getDice().turn + 1;

        receiveResources(dice);
        gameReference.update("players", game.getPlayers());
        gameReference.update("dice", dice);
        chat.sendMessage(new Message(player.getName(), String.format("threw %s", dice.diceOne + dice.diceTwo)));
    }

    public void passTurn () {
        locationsController.setBuildState("null");

        game.nextPlayer();
        game.setTurn(game.getTurn() + 1);

        WriteBatch batch = Firebase.db.batch();
        batch.update(gameReference, "turn", game.getTurn());
        batch.update(gameReference, "currentPlayer", game.getCurrentPlayer());

        batch.commit();

        chat.sendMessage(new Message(player.getName(), "has passed his turn"));
    }

    /**
     * Updates the inventory of the player with the new resources given by a dice throw
     *
     * @author Tijs Groenendaal
     */
    private void receiveResources(Dice dice) {
        int number = dice.diceOne + dice.diceTwo;

        for (Player p : game.getPlayers()) {
            Map<String, Integer> toAdd = new HashMap<>();

            for (Tile tile : locationsController.getTiles()) {
                if (tile.getType().equals("desert")) {
                    continue;
                }

                if (tile.getHasRobber()) {
                    continue;
                }

                for (Location location : tile.getLocations()) {
                    if (location.getColor().equals(p.getColor()) && number == tile.getNumber()) {
                        int count = toAdd.getOrDefault(tile.getType(), 0);
                        if (location.isCity) {
                            toAdd.put(tile.getType(), count + 2);
                        }
                        if (!location.isCity) {
                            toAdd.put(tile.getType(), count + 1);
                        }
                    }
                }
            }

            removeAddResourceFromInventory(toAdd, game.getPlayers().indexOf(p));
        }
    }

    /**
     * Iterates through a Map and updates the items of an players inventory
     * Firebase has to be updated afterwards
     *
     * @param toUpdate Map with all the card that are needed to be updated
     * @param player index of the player
     *
     * @author Tijs Groenendaal
     */
    private void removeAddResourceFromInventory(Map<String, Integer> toUpdate, int player) {
        for (Map.Entry<String, Integer> entry : toUpdate.entrySet()) {
            game.getPlayers().get(player).getInventory().addResource(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Opens the right port window based on the id that has been given
     * so the player can trade with the bank
     *
     * @author Ion Middelraad
     * @author Tijs Groenendaal
     */
    public void openPortTrade(int id) {
        try {
            FXMLLoader loader = SceneManager.getSceneLoader("PortTradeView");
            Parent root = loader.load();
            PortTradeController portTradeController = loader.getController();
            portTradeController.setup(ports[id - 1], game, game.getPlayers().indexOf(player));

            Stage stage = new Stage();
            stage.setTitle("Port Trade");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ignored) {
        }
    }

    //** BUYING / BUILDING **//

    /**
     * Allows the player to buy a random development card.
     * Subtracts the associated resources of the players inventory than adds a random
     * development card to the inventory
     *
     * @author Tijs Groenendaal
     *
     * @throws NotEnoughResourcesException when the player does not have enough resources
     */
    public void buyDevCard() throws NotEnoughResourcesException {
        Map<String, Integer> inventory = player.getInventory().getResourceCards();

        int woolAmount = inventory.get("wool");
        int grainAmount = inventory.get("grain");
        int oreAmount = inventory.get("ore");

        if (woolAmount >= 1 && grainAmount >= 1 && oreAmount >= 1) {
            player.getInventory().buyDevCard();

            // Firebase is updated after addition and subtraction to spare network resources
            gameReference.set(game);
        } else {
            throw new NotEnoughResourcesException();
        }
    }

    /**
     * When the 'road' button is clicked, the players gets shown
     * all possible road locations
     */
    public void buildRoad() throws NotEnoughResourcesException {
        Map<String, Integer> inv = player.getInventory().getResourceCards();
        if (inv.get("lumber") >= 1 && inv.get("brick") >= 1) {
            locationsController.showRoads();
        } else {
            throw new NotEnoughResourcesException();
        }
    }

    /**
     * When the city button is clicked, the player gets
     * shown all possible city locations
     */
    public void buildCity() throws NotEnoughResourcesException {
        Map<String, Integer> inv = player.getInventory().getResourceCards();
        if (inv.get("grain") >= 2 && inv.get("ore") >= 3) {
            locationsController.showCityLocation();
        } else {
            throw new NotEnoughResourcesException();
        }
    }

    /**
     * When the settlement button is clicked, the player gets
     * shown all the possible settlement locations
     */
    public void buildSettlement() throws NotEnoughResourcesException {
        Map<String, Integer> inv = player.getInventory().getResourceCards();

        if (inv.get("lumber") >= 1 && inv.get("brick") >= 1 && inv.get("wool") >= 1 && inv.get("grain") >= 1) {
            locationsController.showSettlementLocations();
        } else {
            throw new NotEnoughResourcesException();
        }
    }

    //** PLAYING DEVELOPMENT CARDS **//

    /**
     * Allows the player to play the monopoly card.
     * Opens a new stage where the player can select a resource to steal from all players
     *
     * @author Tijs Groenendaal
     *
     * @throws NotEnoughDevelopmentCardsException when the player doesn't have this card
     */
    public void playMonopolyCard() throws NotEnoughDevelopmentCardsException{
        if (getDevCardToPlay().getDevelopmentCards().get("monopoly") > 0) {
            FXMLLoader loader = SceneManager.getSceneLoader("MonopolyView");
            try {
                Parent root = loader.load();
                MonopolyController monopolyController = loader.getController();
                monopolyController.setGame(game, game.getPlayers().indexOf(player));
                Stage stage = new Stage();
                stage.setTitle("Monopoly card");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ignored) {
            }
            chat.sendMessage(new Message(String.format("%s used his monopoly card", player.getName())
            ));
        } else {
            throw new NotEnoughDevelopmentCardsException();
        }
    }

    /**
     * Allows the player to get 2 free resources.
     * Opens a new scene where to player can choose 2 resources
     *
     * @author Tijs Groenendaal
     *
     * @throws NotEnoughDevelopmentCardsException when the player does not have this card
     */
    public void playYearOfPlentyCard () throws NotEnoughDevelopmentCardsException {
        if (getDevCardToPlay().getDevelopmentCards().get("year") > 0) {
            try {
                FXMLLoader loader = SceneManager.getSceneLoader("YearOfPlentyView");
                Parent root = loader.load();
                YearOfPlentyController yearOfPlentyController = loader.getController();
                yearOfPlentyController.setGame(game, game.getPlayers().indexOf(player));

                Stage stage = new Stage();
                stage.setTitle("Year of Plenty card");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ignored) {
            }

            chat.sendMessage(new Message(player.getName(), "used his Year of Plenty card"));
        } else {
            throw new NotEnoughDevelopmentCardsException();
        }
    }

    /**
     * Allows the player to build 2 free roads.
     *
     * @author Tijs Groenendaal
     *
     * @throws NotEnoughDevelopmentCardsException when the player doesn't have this card
     */
    public void playRoadsCard() throws NotEnoughDevelopmentCardsException {
        if (getDevCardToPlay().getDevelopmentCards().get("road") > 0) {
            usingRoadCard = true;
            locationsController.setBuildState("roadCard");
            locationsController.showRoads();

            player.getInventory().addDevelopmentCard("road", -1);

            gameReference.set(game);
            chat.sendMessage(new Message(player.getName(), "used his 2 Roads card"));
        } else {
            throw new NotEnoughDevelopmentCardsException();
        }
    }

    /**
     * Let's the player play a knight card.
     * When played a player can move the robber, block a tile and steal a resource.
     * When the players has the biggest knightPower he/she receives 2 points.
     * Those points are lost when another player has the biggest knightPower
     *
     * @author Youp van Leeuwen
     *
     * @throws NotEnoughDevelopmentCardsException when the player does not have this card
     */
    public void playKnightCard () throws NotEnoughDevelopmentCardsException {
        if (getDevCardToPlay().getDevelopmentCards().get("knight") > 0) {
            useKnightCard();
        } else {
            throw new NotEnoughDevelopmentCardsException();
        }
    }

    public void useKnightCard() {
        robber();

        player.getInventory().addDevelopmentCard("knight", -1);
        player.setTotalKnights(player.getTotalKnights() + 1);

        if (player.getTotalKnights() > 2) {
            updateLargestArmy();
        }

        gameReference.update("players", game.getPlayers());

        chat.sendMessage(new Message(player.getName(), "used his Knight card"));
    }

    public void updateLargestArmy () {
        boolean claimed = false;

        for (Player player : game.getPlayers()) {
            if (player.getHasLargestKnight()) {
                claimed = true;

                if (!player.equals(this.player) && this.player.getTotalKnights() > player.getTotalKnights()) {
                    player.setHasLargestKnight(false);
                    player.getInventory().addVictoryPoints(true, -2);

                    this.player.setHasLargestKnight(true);
                    this.player.getInventory().addVictoryPoints(true, 2);
                }
            }
        }

        if (!claimed) {
            this.player.setHasLargestKnight(true);
            this.player.getInventory().addVictoryPoints(true, 2);
        }
    }

    /**
     * Shows available robberLocations to place robber.
     *
     * @author Sean Vis
     */
    public void robber() {
        if (game.getCurrentPlayer() == game.getPlayers().indexOf(player)) {
            locationsController.showRobberLocations();
        }
    }
}