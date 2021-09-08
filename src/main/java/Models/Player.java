package Models;

import com.google.cloud.firestore.annotation.Exclude;

/**
 * @author Youp van Leeuwen
 */
public class Player {
    private String name;
    private String color;
    private Inventory inventory;
    private boolean host = false;
    private int longestRoad;
    private boolean hasLongestRoad = false;
    private int totalKnights;
    private boolean hasLargestKnight = false;

    public Player() {
        this.longestRoad = 0;
        this.totalKnights = 0;
    };

    public Player(String name) {
        this.name = name;
        this.color = "NotImportant";
        this.inventory = new Inventory();
        this.inventory.initializeInventory();
        this.longestRoad = 0;
        this.totalKnights = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Boolean getHost() {
        return host;
    }

    public void setHost(Boolean host) {
        this.host = host;
    }

    public int getLongestRoad() {
        return longestRoad;
    }

    public void setLongestRoad(int longestRoad) {
        this.longestRoad = longestRoad;
    }

    public boolean getHasLongestRoad() {
        return hasLongestRoad;
    }

    public void setHasLongestRoad(boolean hasLongestRoad) {
        this.hasLongestRoad = hasLongestRoad;
    }

    public int getTotalKnights() {
        return totalKnights;
    }

    public void setTotalKnights(int totalKnights) {
        this.totalKnights = totalKnights;
    }

    public boolean getHasLargestKnight() {
        return hasLargestKnight;
    }

    public void setHasLargestKnight(boolean hasLargestKnight) {
        this.hasLargestKnight = hasLargestKnight;
    }

    /**
     * Makes a copy of the player it is called on.
     *
     * @return returns copied player.
     */
    @Exclude
    public Player getCopy() {
        Player player = new Player();
        Inventory inventory = new Inventory();
        inventory.setDevelopmentCards(getInventory().getDevelopmentCards());
        inventory.setResourceCards(getInventory().getResourceCards());
        inventory.setVictoryPoints(getInventory().getVictoryPoints());

        player.setName(getName());
        player.setColor(getColor());
        player.setHost(getHost());
        player.setInventory(inventory);

        return player;
    }
}
