package Models;

import com.google.cloud.firestore.annotation.Exclude;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Location extends AbstactLocation {
    private List<Location> linkedLocations;
    @Exclude
    public List<RoadLocation> roadLocations;
    public Boolean isCity;

    public Location() {}

    public Location(ImageView view) {
        this.view = view;
        this.linkedLocations = new ArrayList<>();
        this.roadLocations = new ArrayList<>();
        this.available = true;
        this.isCity = false;
    }

    @Exclude
    public List<Location> getLinkedLocations() {
        return linkedLocations;
    }

    @Exclude
    public void setLinkedLocations(List<Location> linkedLocation) {
        this.linkedLocations = linkedLocation;
    }

    public void addLocation (Location location) {
        linkedLocations.add(location);
        location.linkedLocations.add(this);
    }

    public Boolean isBuildAble () {
        for (Location location: linkedLocations) {
            if (!location.getAvailable()) {
                return false;
            }
        }

        return available;
    }

    public void buildOnLocation (String color) {
        setAvailable(false);
        this.color = color;
        String settlement = String.format("/images/player/settlement_%s.png", color);
        view.setImage(new Image(settlement));
        view.setVisible(true);
    }

    public void promoteToCity (String color) {
        if (!available) {
            isCity = true;
            this.color = color;
            String city = String.format("/images/player/city_%s.png", color);
            view.setImage(new Image(city));
            view.setVisible(true);
        }
    }
}
