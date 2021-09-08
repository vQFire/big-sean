import Controllers.LobbyController;
import Models.Game;
import Models.Player;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class LobbyTest extends AbstractTest {
    String givenPlayerName = "Piet Paulusma";
    LobbyController lobbyController = new LobbyController();

    @Before
    public void setupLobbyController () {
        lobbyController.setPlayerName(givenPlayerName);
        lobbyController.setGameCode("TESTGAME");
        lobbyController.testing = true;

        lobbyController.inviteLink = new Button();
        lobbyController.saveButton = new Button();
        lobbyController.vcAddBtn = new ImageView();
        lobbyController.vcRmvBtn = new ImageView();
        lobbyController.playerRmvBtn = new ImageView();
        lobbyController.playerAddBtn = new ImageView();
        lobbyController.player1 = new VBox();
        lobbyController.player2 = new VBox();
        lobbyController.player3 = new VBox();
        lobbyController.player4 = new VBox();
        lobbyController.player_player1_name = new Label();
        lobbyController.player_player2_name = new Label();
        lobbyController.player_player3_name = new Label();
        lobbyController.player_player4_name = new Label();
        lobbyController.VPLabel = new Label();
        lobbyController.PlayerAmount = new Label();
        lobbyController.Chat_text = new TextField();
        lobbyController.messagesWrapper = new VBox();
        lobbyController.ChatLog = new VBox();
        lobbyController.lobby_players = new HBox();
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Player_Should_BeHost_WhenHostingGame () {
        lobbyController.hostGame();

        boolean isHost = lobbyController.getLocalPlayer().getHost();

        assertTrue("Player should be host when hosting a game", isHost);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Player_Should_BeColorGreen_WhenHostingGame () {
        lobbyController.hostGame();
        String playerColor = lobbyController.getLocalPlayer().getColor();

        assertEquals("Player's color should be green when hosting game", "green", playerColor);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void PlayerIndex_Should_BeZero_When_HostingAGame () {
        lobbyController.hostGame();
        int playerIndex = lobbyController.getGame().getPlayers().indexOf(lobbyController.getLocalPlayer());

        assertEquals("Player's index should be 0 after joining game", 0, playerIndex);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Player_Should_HaveGivenName_WhenHostingOrJoiningGame () {
        lobbyController.hostGame();
        String playerName = lobbyController.getLocalPlayer().getName();

        String testMessage = String.format("Player's name should be %s when hosting or joining a game", givenPlayerName);
        assertEquals(testMessage, givenPlayerName, playerName);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_ReturnFalse_WhenGameWithGivenCodeDoesNotExist () {
        lobbyController.setGameCode("NO_GAME_WITH_THIS_CODE");

        boolean joinedGame = lobbyController.joinGame();

        assertFalse("Should return false when joining a game that does not exist", joinedGame);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Player_Should_NotBeHost_WhenJoiningGame () {
        lobbyController.joinGame();

        boolean isHost = lobbyController.getLocalPlayer().getHost();

        assertFalse("Player should not be host when joining a game", isHost);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Buttons_Should_BeDisabled_WhenJoiningGame () {
        lobbyController.joinGame();

        assertTrue(lobbyController.saveButton.isDisabled());
        assertTrue(lobbyController.vcAddBtn.isDisabled());
        assertTrue(lobbyController.vcRmvBtn.isDisabled());
        assertTrue(lobbyController.playerAddBtn.isDisabled());
        assertTrue(lobbyController.playerRmvBtn.isDisabled());
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_CopyGameCodeToClipboard_WhenPlayerClicksOnTheGameCodeButton () {
        lobbyController.joinGame();

        Platform.runLater(() -> {
            lobbyController.copyLink();

            final Clipboard CLIPBOARD = Clipboard.getSystemClipboard();

            assertEquals("Clipboard should equal given game code", lobbyController.getGame().getCode(), CLIPBOARD.getString());
        });
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_UpdatePlayers_When_ReceivingUpdateFromFirebase () {
        lobbyController.hostGame();
        Game currentGame = lobbyController.getGame();
        currentGame.getPlayers().clear();
        Player player1 = new Player("Piet");
        Player player2 = new Player("Niet Piet");
        currentGame.addPlayer(player1);
        currentGame.addPlayer(player2);

        lobbyController.update(currentGame);

        assertEquals(2, lobbyController.getGame().getPlayers().size());
    }
}
