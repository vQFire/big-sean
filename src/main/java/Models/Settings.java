package Models;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Lucas Philippi
 */

public class Settings {
    private Map<String, Integer> settings;


    /**
     * Initialized as a variable in the Game model.
     * Contains all changeable settings for a lobby.
     * @param code code for the game lobby
     */
    public Settings(String code) {
        settings = new HashMap<>();
        settings.put("VictoryPoints", 10);
        settings.put("PlayerAmount", 4);
    }

    /**
     * Returns a map containing data from settings
     * @return
     */
    public Map<String, Integer> getSettings() {
        return settings;
    }

    /**
     * Insert new data and/or override settings
     * @param settings
     */
    public void setSettings(Map<String, Integer> settings) {
        this.settings = settings;
    }
}
