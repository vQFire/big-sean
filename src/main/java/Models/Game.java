package Models;

import Application.Firebase;
import Observers.GameObservable;
import Observers.GameObserver;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.EventListener;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Game implements GameObservable {
    private String code;
    private List<Player> players;
    private Boolean gameStarted;
    private Settings settings;
    private Dice dice;
    private int currentPlayer;
    private int turn;

    static private List<GameObserver> observers = new ArrayList<>();

    public Game() {
        this.code = RandomStringUtils.randomAlphabetic(7);
        this.players = new ArrayList<>();
        this.gameStarted = false;
        this.settings = new Settings(code);
        this.dice = new Dice(1,1);
        this.turn = 0;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        Firebase.db.collection("games").document(code).update("players", FieldValue.arrayUnion(player));
        this.getPlayers().add(player);
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void hostGame () {
        addDocumentListener(this.code).set(this);
    }

    public Game joinGame (String code) {
        try {
            return addDocumentListener(code).get().get().toObject(Game.class);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DocumentReference addDocumentListener (String code) {
        DocumentReference documentReference = Firebase.db.collection("games").document(code);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirestoreException e) {
                Game game = documentSnapshot.toObject(Game.class);

                for (GameObserver gameObserver: observers) {
                    gameObserver.update(game);
                }
            }
        });

        return documentReference;
    }

    public void registerObserver (GameObserver gameObserver) {
        observers.add(gameObserver);
    }

    public void removeObserver (GameObserver gameObserver) {
        observers.remove(gameObserver);
    }

    public Boolean getGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(Boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public Map<String, Integer> getSettings() {
        return settings.getSettings();
    }
  
    public void setSettings(Map<String, Integer> setting) {
        settings.setSettings(setting);
    }

    public Dice getDice() {
        return dice;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }

    public void nextPlayer () {
        if (currentPlayer + 1 > getPlayers().size() - 1) {
            setCurrentPlayer(0);
        } else {
            setCurrentPlayer(currentPlayer + 1);
        }
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}
