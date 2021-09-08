package Models;

import com.google.cloud.firestore.annotation.Exclude;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Youp van Leeuwen
 */
public class RoadLocation extends AbstactLocation {
    private Location startLocation;
    private Location endLocation;

    public RoadLocation () {}

    public RoadLocation (Location startLocation, Location endLocation, ImageView view) {
        this.available = true;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.view = view;
        this.color = "";
    }

    @Exclude
    public Location getStartLocation() {
        return startLocation;
    }

    @Exclude
    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    @Exclude
    public Location getEndLocation() {
        return endLocation;
    }

    @Exclude
    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    /**
     * Sets roadLocation to not available, changes image to road.
     *
     * @author Youp van Leeuwen
     *
     * @param color The color of the road.
     */
    public void buildRoad (String color) {
        setAvailable(false);
        this.color = color;
        String road = String.format("/images/player/road_%s.png", color);
        this.view.setImage(new Image(road));
        this.view.setVisible(true);
    }
}
