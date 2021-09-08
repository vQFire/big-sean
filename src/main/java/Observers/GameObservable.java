package Observers;

import Models.Dice;
import Models.Player;

import java.util.List;
import java.util.Map;

public interface GameObservable {
    public List<Player> getPlayers();
    public Boolean getGameStarted();
    public Map<String, Integer> getSettings();
    public Dice getDice();
    public int getCurrentPlayer();
    public int getTurn();
}
