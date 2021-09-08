package Controllers;

import Models.Game;
import Models.Inventory;
import Models.Player;
import com.google.cloud.firestore.DocumentReference;

/**
 * @author Youp van Leeuwen
 */
abstract public class AbstractController {
    protected Game game;
    protected Player player;
    protected DocumentReference gameReference;
    private Inventory devCardToPlay;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGameReference (DocumentReference gameReference) {
        this.gameReference = gameReference;
    }

    public void setDevCardToPlay(Inventory devCardToPlay) {
        this.devCardToPlay = devCardToPlay;
    }

    public Inventory getDevCardToPlay() {
        return devCardToPlay;
    }
}
