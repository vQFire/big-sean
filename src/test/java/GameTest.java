import Application.Firebase;
import Application.SceneManager;
import Controllers.ActionController;
import Controllers.GameController;
import Controllers.LobbyController;
import Exceptions.NotEnoughDevelopmentCardsException;
import Exceptions.NotEnoughResourcesException;
import Models.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.shape.Polygon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class GameTest extends AbstractTest {
    private GameController gameController;

    @Before
    public void setupGameController () {
        try {
            FXMLLoader loader = SceneManager.getSceneLoader("GameView");
            loader.load();

            gameController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    private Game setupGame() {
        Game game = new Game();
        game.setCode("TESTGAME");
        Chat chat = new Chat(game.getCode());
        Player player = new Player("Piet Paulusma");
        player.setHost(true);

        game.getPlayers().add(player);

        gameController.setupGame(chat, game, player);

        return game;
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Game_Should_HaveAnInstanceInFirebase_When_GameHasBeenSetup () {
        Game game = setupGame();

        try {
            boolean inFirebase = Firebase.db.collection("games").document(game.getCode()).get().get().exists();
            Thread.sleep(5000);
            assertTrue(String.format("Game %s has instance in firebase", game.getCode()),inFirebase);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Player_Should_BeAbleToPassTurn_When_ClickingThePassTurnButton () {
        Game game = setupGame();
        Player extraPlayer = new Player("Niet Piet");
        game.addPlayer(extraPlayer);

        int currentTurn = game.getTurn();
        gameController.passTurn();
        int nextTurn = game.getTurn();

        assertNotEquals("After pass turn, the turn should increase", currentTurn, nextTurn);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_SendAMessage_When_PlayersPassesTurn () {
        Game game = setupGame();

        int currentChatLength = Chat.messages.size();
        gameController.passTurn();

        try {
            // Waits for message update from Firebase
            Thread.sleep(5000);
            int newChatLength = Chat.messages.size();
            assertNotEquals("After pass turn, the chat should increase size", currentChatLength, newChatLength);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_ThrowNotEnoughResourcesException_When_PlayerDoesNotHaveEnoughResourcesToBuild () {
        setupGame();

        Platform.runLater(() -> {
            assertThrows(NotEnoughResourcesException.class, () -> gameController.buildRoad());
            assertThrows(NotEnoughResourcesException.class, () -> gameController.buildCity());
            assertThrows(NotEnoughResourcesException.class, () -> gameController.buildSettlement());
            assertThrows(NotEnoughResourcesException.class, () -> gameController.buyDevCard());
        });
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    @Ignore("Don't know how to test not throwing...")
    public void Should_NotThrowNotEnoughResourcesException_When_PlayerHasEnoughResourcesToBuild () {
        Game game = setupGame();
        Player player = gameController.player;
        player.getInventory().addResource("lumber", 10);
        player.getInventory().addResource("brick", 10);
        player.getInventory().addResource("grain", 10);
        player.getInventory().addResource("wool", 10);
        player.getInventory().addResource("ore", 10);

        Platform.runLater(() -> {
            try {
                System.out.println("Failed");
                gameController.buyDevCard();
                gameController.buildRoad();
                gameController.buildCity();
                gameController.buildSettlement();
            } catch (NotEnoughResourcesException exception) {
                System.out.println("Failed");

                fail("Should not have thrown an error");
            }
        });
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_GenerateRandomDice_When_PlayerClicksOnDice () {
        int total = 0;

        ActionController actionController = new ActionController();

        for (int x = 0; x < 100_000; x++) {
            Dice dice = actionController.rollDice();
            total += dice.getDiceOne();
            total += dice.getDiceTwo();
        }

        float averageDiceThrow = (float) total / 100_000;

        assertTrue(averageDiceThrow > 5.5);
        assertTrue(averageDiceThrow < 8.5);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Ignore
    public void Should_ThrowNotEnoughDevelopmentCards_When_PlayerDoesNotHaveEnoughDevelopCards () {
        setupGame();

        Platform.runLater(() -> {
            assertThrows(NotEnoughDevelopmentCardsException.class, () -> gameController.playKnightCard());
            assertThrows(NotEnoughDevelopmentCardsException.class, () -> gameController.playRoadsCard());
            assertThrows(NotEnoughDevelopmentCardsException.class, () -> gameController.playYearOfPlentyCard());
            assertThrows(NotEnoughDevelopmentCardsException.class, () -> gameController.playMonopolyCard());
        });
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_HaveMapWithResourceIcons_When_PlayerStartsGame () {
        setupGame();

        try {
            // Needs to wait for Firebase update
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Polygon[] tiles = gameController.getTilePolies();

        for (Polygon tile: tiles) {
            boolean isImagePattern = tile.getFill().toString().contains("ImagePattern");

            assertTrue(isImagePattern);
        }
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Game_Should_UpdateInventory_When_InventoryIsUpdated_InGame() {
        Game game = setupGame();

        Map<String, Integer> toUpdate = new HashMap<>();
        toUpdate.put("wool", 3);
        toUpdate.put("grain", 1);

        game.getPlayers().get(0).getInventory().removeAddResourceFromInventory(toUpdate);
        assertEquals(4, game.getPlayers().get(0).getInventory().totalResources());
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_CreateTradeRequest_When_PlayerRequestsATrade() {
        setupGame();
        gameController.getTradeController().getNewTrade().get("gives").add("wool");
        gameController.getTradeController().getNewTrade().get("wants").add("lumber");
        Map<String, ArrayList<String>> localTrade = gameController.getTradeController().trade.getTradeStatus();
        gameController.sendTrade();

        try {
            Thread.sleep(5000);
            Map<String, ArrayList<String>> firebaseTrade = gameController.getTradeController().trade.tradeStatus;
            assertNotSame(localTrade, firebaseTrade);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_UpdateInventory_When_DevelopmentCardsIsUpdated_InGame() {
        Game game = setupGame();
        game.getPlayers().get(0).getInventory().addDevelopmentCard("monopoly", 2);

        assertEquals(2, game.getPlayers().get(0).getInventory().totalDevCards());
    }

    /**
     * @author Lucas Philippi
     */
    int currentVictoryPoints, currentPlayerAmount;

    @Test
    public void Should_Create_Settings_When_Game_Is_Created() {
        Game game = setupGame();
        assertEquals(10, game.getSettings().get("VictoryPoints").intValue());
        assertEquals(4, game.getSettings().get("PlayerAmount").intValue());
    }
    /**
     * @author Lucas Philippi
     */
    @Test
    public void Should_Update_VictoryPoint_When_Removing_VictoryPoint() {
        Game game = setupGame();
        currentVictoryPoints = game.getSettings().get("VictoryPoints");
        game.getSettings().put("VictoryPoints", currentVictoryPoints - 1);
        assertEquals(currentVictoryPoints - 1, game.getSettings().get("VictoryPoints").intValue());
    }
    /**
     * @author Lucas Philippi
     */
    @Test
    public void Should_Update_VictoryPoint_When_Adding_VictoryPoint() {
        Game game = setupGame();
        currentVictoryPoints = game.getSettings().get("VictoryPoints");
        game.getSettings().put("VictoryPoints", currentVictoryPoints + 1);
        assertEquals(currentVictoryPoints + 1, game.getSettings().get("VictoryPoints").intValue());
    }
    /**
     * @author Lucas Philippi
     */
    @Test
    public void Should_Update_PlayerAmount_When_Removing_PlayerAmount() {
        Game game = setupGame();
        currentPlayerAmount = game.getSettings().get("PlayerAmount");
        game.getSettings().put("PlayerAmount", currentPlayerAmount - 1);
        assertEquals(currentPlayerAmount - 1, game.getSettings().get("PlayerAmount").intValue());
    }
    /**
     * @author Lucas Philippi
     */
    @Test
    public void Should_Update_PlayerAmount_When_Adding_PlayerAmount() {
        Game game = setupGame();
        currentPlayerAmount = game.getSettings().get("PlayerAmount");
        if (game.getSettings().get("PlayerAmount") == 3) {
            game.getSettings().put("PlayerAmount", currentPlayerAmount + 1);
            assertEquals(currentPlayerAmount + 1, game.getSettings().get("PlayerAmount").intValue());
        } else {
            assertEquals(currentPlayerAmount, game.getSettings().get("PlayerAmount").intValue());
        }
    }
    /**
     * @author Lucas Philippi
     */
    @Test
    public void Should_Update_Settings_When_Updating_Firebase() {
        Game game = setupGame();
        Map<String, Integer> localSettings = game.getSettings();
        localSettings.put("VictoryPoints", 12);
        Firebase.db.collection("games").document(game.getCode()).update("settings", localSettings);
        assertEquals(game.getSettings(), localSettings);
    }
    /**
     * @author Lucas Philippi
     */
    @Test
    public void Update_Largest_Army_When_Playing_Knight_Card() {
        setupGame();
        gameController.player.setTotalKnights(3);
        gameController.getActionController().updateLargestArmy();

        assertEquals(true, gameController.player.getHasLargestKnight());

    }
}
