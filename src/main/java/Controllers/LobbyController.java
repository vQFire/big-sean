package Controllers;

import Application.Firebase;
import Application.SceneManager;
import Factories.MediaPlayerFactory;
import Models.Chat;
import Models.Game;
import Models.Message;
import Models.Player;
import Observers.ChatObservable;
import Observers.ChatObserver;
import Observers.GameObservable;
import Observers.GameObserver;
import com.google.cloud.firestore.FieldValue;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class LobbyController implements GameObserver, ChatObserver {
    public HBox lobby_players;
    public VBox player1, player2, player3, player4;
    public Label player_player1_name, player_player2_name, player_player3_name, player_player4_name;

    public VBox ChatLog;
    public VBox messagesWrapper;
    public TextField Chat_text;
    public Button inviteLink;
    public ScrollPane chatScrollPane;

    private String playerName;
    private String gameCode;
    private Game game;
    private Player localPlayer;
    private int localPlayerIndex;
    private Chat chat;
    private Boolean swappedToGame = false;

    public Label VPLabel, PlayerAmount;
    public Button saveButton;
    public ImageView vcRmvBtn, vcAddBtn, playerRmvBtn, playerAddBtn;

    public boolean testing = false;

    public void setPlayerName (String playerName) {
        this.playerName = playerName;
    }

    public void setGameCode (String gameCode) {
        this.gameCode = gameCode;
    }

    public Player getLocalPlayer () {
        return localPlayer;
    }

    public Game getGame () {
        return game;
    }

    /**
     * Lets the player host a game
     */
    public void hostGame () {
        localPlayer = new Player(playerName);
        localPlayer.setHost(true);
        localPlayer.setColor("green");

        game = new Game();
        game.getPlayers().add(localPlayer);
        localPlayerIndex = game.getPlayers().size() - 1;

        if (testing) {
            game.setCode(gameCode);
        }

        game.hostGame();
        initialiseGame();
    }

    /**
     * Lets a player join a game if there is space and a game could be
     * found with the given code
     *
     * @return When false the game was full or a code could not be found
     */
    public Boolean joinGame () {
        localPlayer = new Player(playerName);

        game = new Game();
        game = game.joinGame(gameCode);

        if (game == null) return false;

        disableButtons();

        if (game.getPlayers().size() < game.getSettings().get("PlayerAmount")) {
            switch (game.getPlayers().size()) {
                case 1: localPlayer.setColor("blue"); break;
                case 2: localPlayer.setColor("red"); break;
                case 3: localPlayer.setColor("gold"); break;
            }

            game.addPlayer(localPlayer);
            localPlayerIndex = game.getPlayers().size() - 1;

            initialiseGame();
            chat.sendMessage(new Message(playerName, "has joined"));
        } else {
            return false;
        }

        return true;
    }

    /**
     * Disables the Setting buttons for players that are NOT the host when player {@link LobbyController#joinGame()}
     *
     * @author Tijs Groenendaal
     */
    private void disableButtons() {
        saveButton.setDisable(true);
        saveButton.setVisible(false);

        vcAddBtn.setDisable(true);
        vcAddBtn.setVisible(false);

        vcRmvBtn.setDisable(true);
        vcRmvBtn.setVisible(false);

        playerAddBtn.setDisable(true);
        playerAddBtn.setVisible(false);

        playerRmvBtn.setDisable(true);
        playerRmvBtn.setVisible(false);
    }

    public void initialiseGame () {
        inviteLink.setText(game.getCode());

        showPlayers(game.getPlayers());

        game.registerObserver(this);
        chat = new Chat(game.getCode());
        chat.registerObserver(this);
    }

    /**
     * Shows each player that has joined the game
     *
     * @param players List of the players in the game
     */
    private void showPlayers (List<Player> players) {
        Platform.runLater(() -> {
            VBox[] playerBoxes = new VBox[]{player1, player2, player3, player4};
            Label[] playerNames = new Label[]{player_player1_name, player_player2_name, player_player3_name, player_player4_name};

            for (int x = 0; x < playerBoxes.length; x++) {
                if (x < players.size()) {
                    playerBoxes[x].setVisible(true);
                    playerNames[x].setText(players.get(x).getName());
                } else {
                    playerBoxes[x].setVisible(false);
                    playerNames[x].setText(String.format("Player %s", x+1));
                }
            }
        });
    }

    public void StartGame(ActionEvent actionEvent) {
        if (!localPlayer.getHost()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,"U bent geen host");
            alert.showAndWait();
        } else if (game.getPlayers().size() < 3) {
            Alert alert = new Alert(Alert.AlertType.WARNING,"Er zijn te weinig spelers");
            alert.showAndWait();
        } else {
            game.setCurrentPlayer(0);
            game.setGameStarted(true);

            Firebase.db.collection("games").document(game.getCode()).set(game);
            chat.sendMessage(new Message("Game has started"));
        }
    }

    /**
     * When in Firebase snapshot @see {@link LobbyController#update(GameObservable)}the {@link Game#getGameStarted()} is True this function is called
     *
     * @author Tijs Groenendaal
     */
    private void goToGame () {
        game.removeObserver(this);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FXMLLoader loader = SceneManager.getSceneLoader("GameView");
                    Parent root = loader.load();
                    GameController gameController = loader.getController();
                    gameController.setupGame(chat, game, localPlayer);

                    SceneManager.addScene("GameView", new Scene(root));
                    SceneManager.setShowingScene("GameView");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * When a new chat message arrives this function is used to add the new message to the chat
     *
     * @param message the message to be added
     */
    private void addChatMessage (Message message) {
        Platform.runLater(() -> {
            HBox messageWrap = new HBox();
            messageWrap.setSpacing(10);
            messageWrap.getChildren().addAll(
                    new Label(message.player),
                    new Label(message.formatTimestamp()),
                    new Label(message.message)
            );

            messagesWrapper.getChildren().add(messageWrap);
        });

        chatScrollPane.setVvalue(1D);
    }

    /**
     * Is called to update the settings to the new victory points
     */
    private void showSettings() {
        Platform.runLater(() -> {
            String victoryPoints = String.valueOf(game.getSettings().get("VictoryPoints"));
            VPLabel.setText(victoryPoints);
            String playerAmount = String.valueOf(game.getSettings().get("PlayerAmount"));
            PlayerAmount.setText(playerAmount);
        });
    }

    //** FXML ACTIONS **//

    /**
     * When the player clicks the 'save' button the new settings are committed to Firebase
     */
    public void saveSettings() {
        if (localPlayer.getHost()) {
            Firebase.db.collection("games").document(game.getCode()).update("settings", game.getSettings());
            showSettings();
        }
    }

    /**
     * When called the system increases the the current victory points by one.
     * This is NOT committed to Firebase until the user saves the score
     */
    public void addVictoryPoint() {
        if (game.getSettings().get("VictoryPoints") < 15) {
            int currentVictoryPoints = game.getSettings().get("VictoryPoints");
            game.getSettings().put("VictoryPoints", currentVictoryPoints + 1);
            showSettings();
        }
    }

    /**
     * When called the system decreases the the current victory points by one.
     * This is NOT committed to Firebase until the user saves the score
     */
    public void removeVictoryPoint() {
        if (game.getSettings().get("VictoryPoints") > 3) {
            int currentVictoryPoints = game.getSettings().get("VictoryPoints");
            game.getSettings().put("VictoryPoints", currentVictoryPoints - 1);
            showSettings();
        }
    }

    /**
     * Sets player amount for Game to 3
     *
     * @author Tijs Groenendaal
     */
    public void removePlayerAmount() {
        if (game.getSettings().get("PlayerAmount") == 4 && game.getPlayers().size() <= 3) {
            int currentPlayerAmount = game.getSettings().get("PlayerAmount");
            game.getSettings().put("PlayerAmount", currentPlayerAmount - 1);
            showSettings();
        }
    }

    /**
     * Sets player amount for Game to 4
     *
     * @author Tijs Groenendaal
     */
    public void addPlayerAmount() {
        if (game.getSettings().get("PlayerAmount") == 3) {
            int currentPlayerAmount = game.getSettings().get("PlayerAmount");
            game.getSettings().put("PlayerAmount", currentPlayerAmount + 1);
            showSettings();
        }
    }

    /**
     * When the 'info' button gets clicked the player is taken the info screen
     */
    public void openInfo() {
        SceneManager.setShowingScene("Info");
    }

    /**
     * When the 'settings' button gets clicked the player is taken the settings screen
     */
    public void openSettings(ActionEvent actionEvent) {
        SceneManager.setShowingScene("Settings");
    }

    /**
     * Called when the user submits a message
     */
    public void SendChat() {
        if (Chat_text.getText().length() > 0) {
            Message message = new Message(playerName, Chat_text.getText());
            chat.sendMessage(message);
            Chat_text.clear();
        }
    }

    /**
     * When clicked the system copies the game code to the player's
     * clipboard, so he/she can share it with friends
     */
    public void copyLink() {
        final Clipboard CLIPBOARD = Clipboard.getSystemClipboard();
        final ClipboardContent CLIPBOARD_CONTENT = new ClipboardContent();
        CLIPBOARD_CONTENT.putString(inviteLink.getText());
        CLIPBOARD.setContent(CLIPBOARD_CONTENT);
    }

    /**
     * This function is called when the user presses the 'back' button
     * The user is returned to the Main Menu and is removed from the firebase
     */
    public void returnTo() {
        try {
            Firebase.db.collection("games").document(this.gameCode).update("players", FieldValue.arrayRemove(localPlayer));
            MediaPlayerFactory.playSound("leave_room.mp3");
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Je kan niet verlaten");
            alert.showAndWait();
        }

        SceneManager.setShowingScene("Main Menu");
    }


    /**
     * When the player pressed Enter when Chat TextField is active sends the message
     *
     * @author Tijs Groenendaal
     *
     * @param keyEvent Contains the source of the Event
     */
    public void chatEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            SendChat();
        }
    }

    //** OBSERVERS **//

    @Override
    public void update(ChatObservable chatObservable) {
        addChatMessage(chatObservable.getLastMessage());
    }

    /**
     * This function is called when a player joins or leaves the game.
     * When called it will update the joined players
     *
     * @param gameObservable contains the updated data
     */
    @Override
    public void update (GameObservable gameObservable) {
        game.setPlayers(gameObservable.getPlayers());
        showPlayers(gameObservable.getPlayers());
        game.setSettings(gameObservable.getSettings());
        showSettings();

        localPlayer = gameObservable.getPlayers().get(localPlayerIndex);

        if (gameObservable.getGameStarted() && !swappedToGame) {
            game.setCurrentPlayer(gameObservable.getCurrentPlayer());
            swappedToGame = true;
            goToGame();
        }
    }
}
