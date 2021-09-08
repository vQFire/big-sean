package Models;

import Exceptions.NotEnoughResourcesToSteal;
import com.google.firebase.database.Exclude;

import java.util.*;

public class Inventory {
    private Map<String, Integer> resourceCards;
    private Map<String, Integer> developmentCards;
    private Map<String, Integer> victoryPoints;

    public Inventory() {}

    public Map<String, Integer> getResourceCards() {
        return resourceCards;
    }

    public void setResourceCards(Map<String, Integer> resourceCards) {
        this.resourceCards = resourceCards;
    }

    public Map<String, Integer> getDevelopmentCards() {
        return developmentCards;
    }

    public void setDevelopmentCards(Map<String, Integer> developmentCards) {
        this.developmentCards = developmentCards;
    }

    public Map<String, Integer> getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(Map<String, Integer> victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    /**
     * Loops through all resources in the inventory.
     * Adds the name of resources where the amount is at least 1 to a list.
     * Picks a random resource(name) from the list.
     * Returns that resource.
     *
     * @author Sean vis
     *
     * @return returns the name of the random resource (to steal).
     */
    @Exclude
    public String randomResource() throws NotEnoughResourcesToSteal {
        List<String> list = new ArrayList<>();
        Random random = new Random();
        for (String string: resourceCards.keySet()) {
            if (resourceCards.get(string) >= 1){
                list.add(string);
            }
        }
        if (list.size() != 0) {
            int index = random.nextInt(list.size());
            return list.get(index);
        } else {
            throw new NotEnoughResourcesToSteal();
        }
    }

    /**
     * Used to add or subtract resources of the players inventory
     *
     * @param resourceName name of the resource
     * @param amount amount that needs to be added or subtracted (Use negative numbers to subtract)
     */
    public void addResource (String resourceName, Integer amount) {
        resourceCards.put(resourceName, resourceCards.get(resourceName) + amount);
    }

    /**
     * Used to add or subtract development cards of the players inventory
     *
     * @param developCardName name of the development card
     * @param amount amount that needs to be added or subtracted (Use negative numbers to subtract)
     */
    public void addDevelopmentCard (String developCardName, Integer amount) {
        developmentCards.put(developCardName, developmentCards.get(developCardName) + amount);
    }

    /**
     * Adds victory points.
     * Adds "Points" to input (hidden or visible) for parsing to firestore.
     *
     * @param visible "hidden" or "visible" depending on the type of point.
     * @param amount amount of points to be gained.
     */
    public void addVictoryPoints(boolean visible, Integer amount) {
        if (visible) {
            int points = victoryPoints.getOrDefault("visiblePoints", 0);

            victoryPoints.put("visiblePoints", points + amount);
        } else {
            int points = victoryPoints.getOrDefault("hiddenPoints", 0);

            victoryPoints.put("hiddenPoints", points + amount);
        }
    }

    /**
     * Iterates through a Map and updates the items of the local players inventory
     * Firebase has to be updated afterwards
     *
     * @param toUpdate Map with all the card that are needed to be updated
     */
    public void removeAddResourceFromInventory(Map<String, Integer> toUpdate) {
        for (Map.Entry<String, Integer> entry : toUpdate.entrySet()) {
            addResource(entry.getKey(), entry.getValue());
        }
    }

    public void buyDevCard() {
        Random random = new Random();
        String[] types = new String[] {
                "monopoly", "monopoly",
                "victory", "victory", "victory", "victory", "victory",
                "knight", "knight", "knight", "knight", "knight", "knight", "knight",
                "knight", "knight", "knight", "knight", "knight", "knight", "knight",
                "road", "road",
                "year", "year"
        };
        String type = types[random.nextInt(25)];
        Map<String, Integer> toRemove = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("wool", -1),
                new AbstractMap.SimpleEntry<>("grain", -1),
                new AbstractMap.SimpleEntry<>("ore", -1)
        );

        if (type.equals("victory")) {
            addVictoryPoints(false, 1);
        }

        removeAddResourceFromInventory(toRemove);
        addDevelopmentCard(type, 1);
    }

    /**
     * Gives the player an empty inventory
     */
    public void initializeInventory() {
        resourceCards = new HashMap<>();
        resourceCards.put("lumber", 0);
        resourceCards.put("wool", 0);
        resourceCards.put("brick", 0);
        resourceCards.put("grain", 0);
        resourceCards.put("ore", 0);

        developmentCards = new HashMap<>();
        developmentCards.put("monopoly", 0);
        developmentCards.put("victory", 0);
        developmentCards.put("knight", 0);
        developmentCards.put("road", 0);
        developmentCards.put("year", 0);

        victoryPoints = new HashMap<>();
        victoryPoints.put("visiblePoints", 0);
        victoryPoints.put("hiddenPoints", 0);
    }

    /**
     * Returns ALL points of a player (visible and hidden points).
     *
     * @author Sean Vis
     *
     * @return returns ALL points of a player (visible and hidden points).
     */
    public int totalResources () {
        int total = 0;

        for (int value: resourceCards.values()) {
            total += value;
        }

        return total;
    }

    public int totalDevCards () {
        int total = 0;

        for (int value: developmentCards.values()) {
            total += value;
        }

        return total;
    }

    public int totalVictoryPoints () {
        int total = 0;

        for (int value: victoryPoints.values()) {
            total += value;
        }

        return total;
    }
}
