package Models;

import com.google.cloud.firestore.annotation.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tijs Groenendaal
 */
public class Tile {
    private String type;
    private boolean hasRobber;
    private int number;
    private List<Location> locations;

    /**
     * Empty constructor for Firebase .toObject function
     */
    public Tile() {
        this.locations = new ArrayList<>();
    }

    /**
     *
     * @param type desert, lumber, brick, ore, wool, grain
     * @param number the number that is assigned to this Tile, used for receiving resources when dice is thrown
     */
    public Tile(String type, boolean hasRobber, int number) {
        this.type = type;
        this.hasRobber = hasRobber;
        this.number = number;
        this.locations = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getHasRobber() {
        return hasRobber;
    }

    public void setHasRobber(boolean hasRobber) {
        this.hasRobber = hasRobber;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Exclude
    public List<Location> getLocations() {
        return locations;
    }

    @Exclude
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location location, int... locationsNumbers) {
        locations.add(location);
    }
}
