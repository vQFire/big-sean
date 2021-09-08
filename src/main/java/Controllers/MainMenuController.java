package Controllers;

import Application.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MainMenuController {
    public TextField InviteLink;
    private String inviteCode;

    public TextField playerName;
    public String playerNameText;

    /**
     * Called when the user clicks the host game button in the main menu.
     * Calls the checkCode method, to verify the length of the code.
     * Then creates the game
     */
    public void HostGame() {
        try {
            checkName();

            FXMLLoader loader = SceneManager.getSceneLoader("LobbyView");
            Parent root = loader.load();

            LobbyController lobbyController = loader.getController();
            lobbyController.setPlayerName(playerNameText);
            lobbyController.hostGame();

            SceneManager.addScene("Lobby", new Scene(root));
            SceneManager.setShowingScene("Lobby");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the user clicks the host game button in the main menu.
     * Calls the checkCode method, to verify the length of the code.
     * Then joins the game
     */
    public void JoinGame() {
        if (checkCode()) {
            try {
                checkName();
                FXMLLoader loader = SceneManager.getSceneLoader("LobbyView");
                Parent root = loader.load();

                LobbyController lobbyController = loader.getController();
                lobbyController.setGameCode(inviteCode);
                lobbyController.setPlayerName(playerNameText);
                if (lobbyController.joinGame()) {
                    SceneManager.addScene("Lobby", new Scene(root));
                    SceneManager.setShowingScene("Lobby");
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "The game is either full or this is an unknown code");
                    alert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "This code is invalid");
            alert.showAndWait();
        }
    }

    /**
     * @return Returns true is code is 7 characters
     */
    public Boolean checkCode() {
        inviteCode = this.InviteLink.getText();
        inviteCode = inviteCode.replaceAll(" ", "");

        return inviteCode.length() == 7;
    }

    /**
     * Checks the players name, if empty name is "John Deere"
     */
    public void checkName() {
        playerNameText = playerName.getText().length() > 0 ? playerName.getText() : "John Deere";
    }

    public void openSettings(ActionEvent actionEvent) {
        SceneManager.setShowingScene("Settings");
    }

    public void openInfo(ActionEvent actionEvent) {
        SceneManager.setShowingScene("Info");
    }
}
