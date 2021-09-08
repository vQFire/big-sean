package Models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * @author Sean Vis
 */
public class RobberLocation extends AbstactLocation{

    public RobberLocation() { }

    public RobberLocation(ImageView view){
        this.view = view;
        this.available = true;
    }

    /**
     * Places robber on the new spot and removes robber from the old spot.
     * Loops through all robberLocations, sets the old robberLocation to an empty robberLocation.
     * Sets new robberLocation to not available, sets image to robber icon.
     *
     * @author Sean Vis
     *
     * @param robberLocation robberLocation the robber is moved to.
     * @param robberLocations list of ALL robberLocations on the map.
     */
    public void placeRobber(RobberLocation robberLocation, List<RobberLocation> robberLocations) {
        for (RobberLocation Location : robberLocations) {
            if (!Location.getAvailable()) {
                Location.setAvailable(true);
                Location.getView().setVisible(false);
                Location.getView().setImage(null);
            }
        }
        robberLocation.setAvailable(false);
        robberLocation.getView().setVisible(true);
        robberLocation.getView().setImage(new Image("images/tiles/numbers/icon_robber.png"));
    }
}
