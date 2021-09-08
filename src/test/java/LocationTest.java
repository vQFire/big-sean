import Application.Firebase;
import Application.SceneManager;
import Controllers.GameController;
import Controllers.LocationsController;
import Models.*;
import javafx.fxml.FXMLLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class LocationTest extends AbstractTest {
    private GameController gameController;
    private LocationsController locationsController;

    @Before
    public void setupGameController () {
        try {
            FXMLLoader loader = SceneManager.getSceneLoader("GameView");
            loader.load();

            gameController = loader.getController();
            locationsController = new LocationsController(gameController.buildingLocations, gameController.roadLocations, gameController.robberLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_HaveDefaultAmountOfLocations_When_SettingUpGame () {
        int totalLocations = locationsController.getLocations().size();

        assertEquals(54, totalLocations);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_HaveDefaultAmountOfRoadLocations_When_SettingUpGame () {
        int totalRoadLocations = locationsController.getRoadLocations().size();

        assertEquals(72, totalRoadLocations);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_HaveDefaultAmountOfRobberLocation_When_SettingUpGame () {
        int totalRobberLocations = locationsController.getRobberLocations().size();

        assertEquals(19, totalRobberLocations);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void EachLocation_Should_HaveAtLeastTwoLocation_When_SettingUpGame () {
        List<Location> locations = locationsController.getLocations();

        for (Location location: locations) {
            int totalLocations = location.getLinkedLocations().size();
            boolean moreThanTwoLinkedLocations = totalLocations >= 2;
            boolean lessThanTwoLinkedLocations = totalLocations <= 3;

            assertTrue("Each location must have more than 2 linked locations", moreThanTwoLinkedLocations);
            assertTrue("Each location must have less than 3 linked locations", lessThanTwoLinkedLocations);
        }
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Location_Should_NotBeBuildable_When_OneOfTheLinkedLocationsHasBeenBuildOn () {
        for (Location startLocation: locationsController.getLocations()) {
            startLocation.setAvailable(false); // Defines that something has been build

            for (Location linkedLocation: startLocation.getLinkedLocations()) {
                boolean isBuildable = linkedLocation.isBuildAble();
                assertFalse(String.format("Locations %s is still buildable", locationsController.getLocations().indexOf(linkedLocation) + 1), isBuildable);
            }

            startLocation.setAvailable(true); // Resets the locations
        }
    }

    private void setupGame() {
        Models.Game game = new Game();
        game.setCode("TESTGAME");
        Chat chat = new Chat(game.getCode());
        Player player = new Player("Piet Paulusma");
        player.setHost(true);
        player.setColor("green");

        game.getPlayers().add(player);

        gameController.setupGame(chat, game, player);

        locationsController.setGame(game);
        locationsController.setPlayer(player);
        locationsController.gameReference = Firebase.db.collection("games").document(game.getCode());
    }

    @Test
    public void Should_CalculateLongestRoute_When_PlayerBuildsARoad () {
        Location startLocation = locationsController.getLocations().get(0);
        startLocation.setAvailable(false);
        startLocation.setColor("green");

        RoadLocation roadLocation1 = locationsController.getRoadLocations().get(0);
        RoadLocation roadLocation2 = locationsController.getRoadLocations().get(1);
        RoadLocation roadLocation7 = locationsController.getRoadLocations().get(6);
        RoadLocation roadLocation8 = locationsController.getRoadLocations().get(7);

        RoadLocation[] roadLocations = new RoadLocation[]{roadLocation1, roadLocation2, roadLocation7, roadLocation8};

        for (RoadLocation roadLocation: roadLocations) {
            roadLocation.setAvailable(false);
            roadLocation.setColor("green");
        }

        setupGame();

        locationsController.longestRoad();

        int longestRoad = gameController.player.getLongestRoad();

        assertEquals("Longest road should be 4", 4, longestRoad);
    }

    @Test
    public void Should_NotInfinitelyLoop_When_PlayerBuildsARoadInACircle () {
        Location startLocation = locationsController.getLocations().get(0);
        startLocation.setAvailable(false);
        startLocation.setColor("green");

        RoadLocation roadLocation1 = locationsController.getRoadLocations().get(0);
        RoadLocation roadLocation2 = locationsController.getRoadLocations().get(1);
        RoadLocation roadLocation7 = locationsController.getRoadLocations().get(6);
        RoadLocation roadLocation8 = locationsController.getRoadLocations().get(7);
        RoadLocation roadLocation12 = locationsController.getRoadLocations().get(11);
        RoadLocation roadLocation13 = locationsController.getRoadLocations().get(12);

        RoadLocation[] roadLocations = new RoadLocation[]{roadLocation1, roadLocation2, roadLocation7, roadLocation8, roadLocation12, roadLocation13};

        for (RoadLocation roadLocation: roadLocations) {
            roadLocation.setAvailable(false);
            roadLocation.setColor("green");
        }

        setupGame();

        locationsController.longestRoad();

        int longestRoad = gameController.player.getLongestRoad();

        assertEquals("Longest road should be 6", 6, longestRoad);
    }
}
