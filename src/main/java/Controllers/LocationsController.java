package Controllers;

import Application.Firebase;
import Exceptions.NotEnoughResourcesToSteal;
import Models.*;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.EventListener;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class LocationsController extends AbstractController {
    private List<Location> locations = new ArrayList<>();
    private List<RoadLocation> roadLocations = new ArrayList<>();
    private List<RobberLocation> robberLocations = new ArrayList<>();

    /**
     * List of the checked road, used in the recursive functions
     */
    private List<RoadLocation> checkedRoads;

    /**
     * Image for a possible location to build or place on
     */
    private final Image GRAY_CIRCLE = new Image("/images/tiles/gray_circle.png");

    /**
     * State keeps track of which action is being done
     * Possible states: settlement, freeSettlement, city, road, freeRoad
     */
    private String state;
    private String buildState = "null";

    /**
     * Used in the startPhase of the game to keep track of the next player
     */
    public int nextPlayer;
    private List<Tile> tiles;
    public DocumentReference gameReference;
    private int longestRoad = -1;

    /**
     * Creates all locations, roadLocations and robberLocations.
     * Links all the locations, robber and road locations.
     *
     * @author Youp van Leeuwen
     * @author Sean Vis
     * @author Tijs Groenendaal
     *
     * @param locationViews all the JavaFX location imageViews
     * @param roadLocationViews all the JavaFX roadLocations imageViews
     * @param robberLocationViews all the JavaFX robberLocation imageViews
     */
    public LocationsController(ImageView[] locationViews, ImageView[] roadLocationViews, ImageView[] robberLocationViews) {
        // ROW 1
        Location location1 = createLocation(locationViews[0]);
        Location location2 = createLocation(locationViews[1], location1);
        Location location3 = createLocation(locationViews[2], location2);
        Location location4 = createLocation(locationViews[3], location3);
        Location location5 = createLocation(locationViews[4], location4);
        Location location6 = createLocation(locationViews[5], location5);
        Location location7 = createLocation(locationViews[6], location6);

        createRoadLocation(location1, location2, roadLocationViews[0]);
        createRoadLocation(location2, location3, roadLocationViews[1]);
        createRoadLocation(location3, location4, roadLocationViews[2]);
        createRoadLocation(location4, location5, roadLocationViews[3]);
        createRoadLocation(location5, location6, roadLocationViews[4]);
        createRoadLocation(location6, location7, roadLocationViews[5]);

        createRobberLocation(robberLocationViews[0]);
        createRobberLocation(robberLocationViews[1]);
        createRobberLocation(robberLocationViews[2]);

        // ROW 2
        Location location8 = createLocation(locationViews[7]);
        Location location9 = createLocation(locationViews[8], location1, location8);
        Location location10 = createLocation(locationViews[9], location9);
        Location location11 = createLocation(locationViews[10], location3, location10);
        Location location12 = createLocation(locationViews[11], location11);
        Location location13 = createLocation(locationViews[12], location5, location12);
        Location location14 = createLocation(locationViews[13], location13);
        Location location15 = createLocation(locationViews[14], location7, location14);
        Location location16 = createLocation(locationViews[15], location15);

        createRoadLocation(location1, location9, roadLocationViews[6]);
        createRoadLocation(location3, location11, roadLocationViews[7]);
        createRoadLocation(location5, location13, roadLocationViews[8]);
        createRoadLocation(location7, location15, roadLocationViews[9]);
        createRoadLocation(location8, location9, roadLocationViews[10]);
        createRoadLocation(location9, location10, roadLocationViews[11]);
        createRoadLocation(location10, location11, roadLocationViews[12]);
        createRoadLocation(location11, location12, roadLocationViews[13]);
        createRoadLocation(location12, location13, roadLocationViews[14]);
        createRoadLocation(location13, location14, roadLocationViews[15]);
        createRoadLocation(location14, location15, roadLocationViews[16]);
        createRoadLocation(location15, location16, roadLocationViews[17]);

        createRobberLocation(robberLocationViews[3]);
        createRobberLocation(robberLocationViews[4]);
        createRobberLocation(robberLocationViews[5]);
        createRobberLocation(robberLocationViews[6]);

        // ROW 3
        Location location17 = createLocation(locationViews[16]);
        Location location18 = createLocation(locationViews[17], location8, location17);
        Location location19 = createLocation(locationViews[18], location18);
        Location location20 = createLocation(locationViews[19], location10, location19);
        Location location21 = createLocation(locationViews[20], location20);
        Location location22 = createLocation(locationViews[21], location12, location21);
        Location location23 = createLocation(locationViews[22], location22);
        Location location24 = createLocation(locationViews[23], location14, location23);
        Location location25 = createLocation(locationViews[24], location24);
        Location location26 = createLocation(locationViews[25], location16, location25);
        Location location27 = createLocation(locationViews[26], location26);

        createRoadLocation(location8, location18, roadLocationViews[18]);
        createRoadLocation(location10, location20, roadLocationViews[19]);
        createRoadLocation(location12, location22, roadLocationViews[20]);
        createRoadLocation(location14, location24, roadLocationViews[21]);
        createRoadLocation(location16, location26, roadLocationViews[22]);
        createRoadLocation(location17, location18, roadLocationViews[23]);
        createRoadLocation(location18, location19, roadLocationViews[24]);
        createRoadLocation(location19, location20, roadLocationViews[25]);
        createRoadLocation(location20, location21, roadLocationViews[26]);
        createRoadLocation(location21, location22, roadLocationViews[27]);
        createRoadLocation(location22, location23, roadLocationViews[28]);
        createRoadLocation(location23, location24, roadLocationViews[29]);
        createRoadLocation(location24, location25, roadLocationViews[30]);
        createRoadLocation(location25, location26, roadLocationViews[31]);
        createRoadLocation(location26, location27, roadLocationViews[32]);

        createRobberLocation(robberLocationViews[7]);
        createRobberLocation(robberLocationViews[8]);
        createRobberLocation(robberLocationViews[9]);
        createRobberLocation(robberLocationViews[10]);
        createRobberLocation(robberLocationViews[11]);

        // ROW 4
        Location location28 = createLocation(locationViews[27], location17);
        Location location29 = createLocation(locationViews[28], location28);
        Location location30 = createLocation(locationViews[29], location19, location29);
        Location location31 = createLocation(locationViews[30], location30);
        Location location32 = createLocation(locationViews[31], location21, location31);
        Location location33 = createLocation(locationViews[32], location32);
        Location location34 = createLocation(locationViews[33], location23, location33);
        Location location35 = createLocation(locationViews[34], location34);
        Location location36 = createLocation(locationViews[35], location25, location35);
        Location location37 = createLocation(locationViews[36], location36);
        Location location38 = createLocation(locationViews[37], location27, location37);

        createRoadLocation(location17, location28, roadLocationViews[33]);
        createRoadLocation(location19, location30, roadLocationViews[34]);
        createRoadLocation(location21, location32, roadLocationViews[35]);
        createRoadLocation(location23, location34, roadLocationViews[36]);
        createRoadLocation(location25, location36, roadLocationViews[37]);
        createRoadLocation(location27, location38, roadLocationViews[38]);
        createRoadLocation(location28, location29, roadLocationViews[39]);
        createRoadLocation(location29, location30, roadLocationViews[40]);
        createRoadLocation(location30, location31, roadLocationViews[41]);
        createRoadLocation(location31, location32, roadLocationViews[42]);
        createRoadLocation(location32, location33, roadLocationViews[43]);
        createRoadLocation(location33, location34, roadLocationViews[44]);
        createRoadLocation(location34, location35, roadLocationViews[45]);
        createRoadLocation(location35, location36, roadLocationViews[46]);
        createRoadLocation(location36, location37, roadLocationViews[47]);
        createRoadLocation(location37, location38, roadLocationViews[48]);

        createRobberLocation(robberLocationViews[12]);
        createRobberLocation(robberLocationViews[13]);
        createRobberLocation(robberLocationViews[14]);
        createRobberLocation(robberLocationViews[15]);

        // ROW 5
        Location location39 = createLocation(locationViews[38], location29);
        Location location40 = createLocation(locationViews[39], location39);
        Location location41 = createLocation(locationViews[40], location31, location40);
        Location location42 = createLocation(locationViews[41], location41);
        Location location43 = createLocation(locationViews[42], location33, location42);
        Location location44 = createLocation(locationViews[43], location43);
        Location location45 = createLocation(locationViews[44], location35, location44);
        Location location46 = createLocation(locationViews[45], location45);
        Location location47 = createLocation(locationViews[46], location37, location46);

        createRoadLocation(location29, location39, roadLocationViews[49]);
        createRoadLocation(location31, location41, roadLocationViews[50]);
        createRoadLocation(location33, location43, roadLocationViews[51]);
        createRoadLocation(location35, location45, roadLocationViews[52]);
        createRoadLocation(location37, location47, roadLocationViews[53]);
        createRoadLocation(location39, location40, roadLocationViews[54]);
        createRoadLocation(location40, location41, roadLocationViews[55]);
        createRoadLocation(location41, location42, roadLocationViews[56]);
        createRoadLocation(location42, location43, roadLocationViews[57]);
        createRoadLocation(location43, location44, roadLocationViews[58]);
        createRoadLocation(location44, location45, roadLocationViews[59]);
        createRoadLocation(location45, location46, roadLocationViews[60]);
        createRoadLocation(location46, location47, roadLocationViews[61]);

        createRobberLocation(robberLocationViews[16]);
        createRobberLocation(robberLocationViews[17]);
        createRobberLocation(robberLocationViews[18]);

        // ROW 6
        Location location48 = createLocation(locationViews[47], location40);
        Location location49 = createLocation(locationViews[48], location48);
        Location location50 = createLocation(locationViews[49], location42, location49);
        Location location51 = createLocation(locationViews[50], location50);
        Location location52 = createLocation(locationViews[51], location44, location51);
        Location location53 = createLocation(locationViews[52], location52);
        Location location54 = createLocation(locationViews[53], location46, location53);

        createRoadLocation(location40, location48, roadLocationViews[62]);
        createRoadLocation(location42, location50, roadLocationViews[63]);
        createRoadLocation(location44, location52, roadLocationViews[64]);
        createRoadLocation(location46, location54, roadLocationViews[65]);
        createRoadLocation(location48, location49, roadLocationViews[66]);
        createRoadLocation(location49, location50, roadLocationViews[67]);
        createRoadLocation(location50, location51, roadLocationViews[68]);
        createRoadLocation(location51, location52, roadLocationViews[69]);
        createRoadLocation(location52, location53, roadLocationViews[70]);
        createRoadLocation(location53, location54, roadLocationViews[71]);
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    /**
     * Creates a location with an event
     *
     * @author Youp van Leeuwen
     *
     * @param view the JavaFX view
     * @param locations the locations that need to be add to the new location
     * @return the created location to be used for linking to other locations
     */
    private Location createLocation(ImageView view, Location... locations) {
        Location temporaryLocation = new Location(view);

        for (Location location : locations) {
            temporaryLocation.addLocation(location);
        }

        temporaryLocation.getView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (state.equals("settlement") && temporaryLocation.getAvailable()) {
                    temporaryLocation.buildOnLocation(player.getColor());
                    increasePlayerScore();
                }

                if (state.equals("city") && !temporaryLocation.getAvailable()) {
                    temporaryLocation.promoteToCity(player.getColor());
                    increasePlayerScore();
                }

                if (state.equals("freeSettlement")) {
                    removeGrey();
                    temporaryLocation.buildOnLocation(player.getColor());
                    showAvailableRoads(temporaryLocation, true);

                    if (player.getInventory().totalVictoryPoints() == 1) {
                        for (Tile tile: tiles) {
                            if (!tile.getType().equals("desert") && tile.getLocations().contains(temporaryLocation)) {
                                Inventory playerInventory = player.getInventory();
                                String resource = tile.getType();
                                playerInventory.addResource(resource, 1);
                            }
                        }
                    }
                }

                if (state.equals("stealing")) {
                    removeGrey();
                    steal(temporaryLocation);
                }
            }
        });

        this.locations.add(temporaryLocation);
        return temporaryLocation;
    }

    /**
     * Creates a road location with an click event
     *
     * @author Youp van Leeuwen
     *
     * @param startLocation the location where the road starts (order doesn't really matter)
     * @param endLocation the location where the road starts (order doesn't really matter)
     * @param view the view from JavaFX associated with the locations
     */
    private void createRoadLocation(Location startLocation, Location endLocation, ImageView view) {
        RoadLocation roadLocation = new RoadLocation(startLocation, endLocation, view);
        startLocation.roadLocations.add(roadLocation);
        endLocation.roadLocations.add(roadLocation);

        roadLocation.getView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (state.equals("road") && roadLocation.getAvailable()) {
                    roadLocation.buildRoad(player.getColor());
                    removeGrey();
                    updateFirebaseData(Firebase.db.batch());
                    longestRoad();
                }

                if (state.equals("freeRoad") && roadLocation.getAvailable()) {
                    roadLocation.buildRoad(player.getColor());
                    player.getInventory().addVictoryPoints(true, 1);

                    removeGrey();
                    WriteBatch batch = Firebase.db.batch();

                    batch.update(gameReference, "currentPlayer", nextPlayer);
                    batch.update(gameReference, "players", game.getPlayers());
                    batch.update(gameReference, "turn", game.getTurn() + 1);

                    updateFirebaseData(batch);
                }
            }
        });

        roadLocations.add(roadLocation);
    }

    /**
     * Creates a robber location on the game map.
     * MouseEvent places robber on clicked location.
     * After robber is placed, showStealingLocations(newRobberTile)
     *
     * @author Sean Vis
     *
     * @param view The imageView connected to the robberLocation on the map.
     */
    private void createRobberLocation(ImageView view) {
        RobberLocation robberLocation = new RobberLocation(view);

        robberLocation.getView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (state.equals("robber") && robberLocation.getAvailable() && !tiles.get(robberLocations.indexOf(robberLocation)).getType().equals("desert")) {
                    robberLocation.placeRobber(robberLocation, robberLocations);
                    Tile newRobberTile = tiles.get(robberLocations.indexOf(robberLocation));
                    for (Tile tile : tiles) {
                        tile.setHasRobber(false);
                    }
                    newRobberTile.setHasRobber(true);
                    removeGrey();
                    gameReference.collection("map").document("map").update("tiles", tiles);
                    showStealingLocations(newRobberTile);
                }
            }
        });
        robberLocations.add(robberLocation);
    }

    /**
     * Shows all available robber locations as a gray circle.
     * Doesn't show: current robber location, desert robber location.
     *
     * @author Sean Vis
     */
    public void showRobberLocations() {
        removeGrey();

        state = "robber";
        for (RobberLocation robberLocation : robberLocations) {
            if (robberLocation.getAvailable() && !tiles.get(robberLocations.indexOf(robberLocation)).getType().equals("desert")) {
                robberLocation.getView().setVisible(true);
                robberLocation.getView().setImage(GRAY_CIRCLE);
            }
        }
    }

    /**
     * Shows all the settlements you can steal from.
     * Shows ONLY houses where the connected player has at least one resource.
     * Skips the players' own houses.
     * If there is no eligible location to steal from, stealing sequence is skipped.
     *
     * @author Sean Vis
     *
     * @param tile The tile that contains the robber.
     */
    public void showStealingLocations(Tile tile) {
        removeGrey();

        boolean playerFound = false;

        state = "stealing";
        for (Location location: tile.getLocations()) {
            if (!location.getAvailable() && !location.getColor().equals(player.getColor())) {
                for (Player player: game.getPlayers()){
                    if (player.getInventory().totalResources() >= 1) {
                        location.getView().setImage(GRAY_CIRCLE);
                        location.getView().setVisible(true);
                        playerFound = true;
                    }
                }
            }
        }
        if (!playerFound) {
            removeGrey();
        }
    }

    /**
     * Steals a random resource from the player connected to the location.
     * Removes the resource from the target and adds it to the local player.
     *
     * @author Sean Vis
     *
     * @param location Location (settlement/city) to steal from.
     */
    private void steal(Location location) {
        if (!player.getColor().equals(location.getColor()) && !location.getAvailable()) {
            for (Player player: game.getPlayers()) {
                if (player.getColor().equals(location.getColor()) && this.player != player) {
                    String resource = null;
                    try {
                        resource = player.getInventory().randomResource();
                        player.getInventory().addResource(resource, -1);
                        this.player.getInventory().addResource(resource, 1);
                    } catch (NotEnoughResourcesToSteal ignored) {
                    }
                }
            }
            WriteBatch writeBatch = Firebase.db.batch();
            writeBatch.update(gameReference, "players", game.getPlayers());
            updateFirebaseData(writeBatch);
        }
    }

    /**
     * Called when the placed builds a settlement or city.
     * Increases the players score by one and updates Firebase
     *
     * @author Youp van Leeuwen
     */
    private void increasePlayerScore() {
        player.getInventory().addVictoryPoints(true, 1);

        WriteBatch batch = Firebase.db.batch();
        batch.update(gameReference, "players", game.getPlayers());

        removeGrey();
        updateFirebaseData(batch);
    }

    /**
     * Finds each players already placed settlements and starts finding new possible places
     *
     * @author Youp van Leeuwen
     */
    public void showSettlementLocations() {
        removeGrey();

        buildState = "settlement";

        for (Location location : locations) {
            if (!location.getAvailable() && location.getColor().equals(player.getColor())) {
                checkedRoads = new ArrayList<>();
                showAvailableSettlementLocations(location);
            }
        }
    }

    /**
     * Recursive function to find all the possible settlement locations.
     * A settlement can only be build at the end or between roads and
     * there must be at least one empty location in between.
     *
     * @author Youp van Leeuwen
     *
     * @param location the location that needs to be checked for availability and adjacent roads
     */
    private void showAvailableSettlementLocations(Location location) {
        for (RoadLocation roadLocation : location.roadLocations) {
            if ((!roadLocation.getAvailable() && roadLocation.getColor().equals(player.getColor())) && !checkedRoads.contains(roadLocation)) {
                checkedRoads.add(roadLocation);

                if (roadLocation.getStartLocation().equals(location)) {
                    if (roadLocation.getEndLocation().isBuildAble()) {
                        state = "settlement";
                        roadLocation.getEndLocation().getView().setImage(GRAY_CIRCLE);
                        roadLocation.getEndLocation().getView().setVisible(true);
                    }

                    showAvailableSettlementLocations(roadLocation.getEndLocation());
                } else {
                    if (roadLocation.getStartLocation().isBuildAble()) {
                        state = "settlement";
                        roadLocation.getStartLocation().getView().setImage(GRAY_CIRCLE);
                        roadLocation.getStartLocation().getView().setVisible(true);
                    }

                    showAvailableSettlementLocations(roadLocation.getStartLocation());
                }
            }
        }
    }

    /**
     * Shows all the possible city locations. A city can only be built on
     * another settlement.
     *
     * @author Youp van Leeuwen
     */
    public void showCityLocation() {
        removeGrey();

        buildState = "city";

        for (Location location: locations) {
            if (location.getColor().equals(player.getColor()) && !location.isCity) {
                location.getView().setImage(GRAY_CIRCLE);
                location.getView().setVisible(true);

                state = "city";
            }
        }
    }

    /**
     * Finds all the settlement or cities placed by the player.
     * For each settlement or city the showAvailableRoads is called.
     *
     * @author Youp van Leeuwen
     */
    public void showRoads() {
        removeGrey();

        if (buildState.equals("null")) buildState = "road";

        for (Location location : locations) {
            if (!location.getAvailable() && location.getColor().equals(player.getColor())) {
                checkedRoads = new ArrayList<>();
                showAvailableRoads(location, false);
            }
        }
    }

    /**
     * Recursive function to show all available locations to build a road on.
     * A road must be build next to another road, settlement or location.
     *
     * @author Youp van Leeuwen
     *
     * @param location the location that needs to be checked on linked roads
     * @param freeRoad Sets build state according this value true: "freeRoad", false: "road"
     */
    private void showAvailableRoads(Location location, boolean freeRoad) {
        for (RoadLocation roadLocation : location.roadLocations) {
            if (roadLocation.getAvailable()) {
                state = freeRoad ? "freeRoad" : "road";

                roadLocation.getView().setVisible(true);
                roadLocation.getView().setImage(GRAY_CIRCLE);
            } else {
                if (!checkedRoads.contains(roadLocation) && roadLocation.getColor().equals(player.getColor())) {
                    checkedRoads.add(roadLocation);

                    showAvailableRoads(roadLocation.getStartLocation(), false);
                    showAvailableRoads(roadLocation.getEndLocation(), false);
                }
            }
        }
    }

    /**
     * Shows all the possible locations to build on in the start phase.
     * This means that a settlement does not have to be build next to a road.
     *
     * @author Youp van Leeuwen
     */
    public void showFreeSettlement() {
        removeGrey();

        state = "freeSettlement";

        for (Location location : locations) {
            if (location.isBuildAble()) {
                location.getView().setImage(GRAY_CIRCLE);
                location.getView().setVisible(true);
            }
        }
    }

    /**
     * Removed all the gray (Possible build or place locations) from the screen
     *
     * @author Youp van Leeuwen
     * @author Sean Vis
     */
    private void removeGrey() {
        state = "done";

        for (Location location : locations) {
            if (location.getAvailable()) {
                location.getView().setVisible(false);
            }
            if (!location.getAvailable() && !location.isCity) {
                String settlement = String.format("/images/player/settlement_%s.png", location.getColor());
                location.getView().setImage(new Image(settlement));
            }
            if (!location.getAvailable() && location.isCity) {
                String city = String.format("/images/player/city_%s.png", location.getColor());
                location.getView().setImage(new Image(city));
            }
        }

        for (RoadLocation roadLocation : roadLocations) {
            if (roadLocation.getAvailable()) {
                roadLocation.getView().setVisible(false);
            }
        }

        for (RobberLocation robberLocation : robberLocations) {
            if (robberLocation.getAvailable()) {
                robberLocation.getView().setVisible(false);
            }
        }
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<RoadLocation> getRoadLocations() {
        return roadLocations;
    }

    public void setRoadLocations(List<RoadLocation> roadLocations) {
        this.roadLocations = roadLocations;
    }

    public List<RobberLocation> getRobberLocations() {
        return robberLocations;
    }

    public void setRobberLocations(List<RobberLocation> robberLocations) {
        this.robberLocations = robberLocations;
    }

    public void setBuildState(String state) {
        this.buildState = state;
    }

    /**
     * Called when an update is received from Firebase.
     * Shows the new buildings that have been build by other players
     *
     * @author Youp van Leeuwen
     *
     * @param locations the new locations data
     * @param roadLocations the new roadLocations data
     */
    public void updateLocations (List<Map<String, Object>> locations, List<Map<String, Object>> roadLocations) {
        if (locations == null) return;

        Platform.runLater(() -> {
            for (int x = 0; x < locations.size(); x++) {
                Map<String, Object> data = locations.get(x);
                Location location = this.locations.get(x);

                location.setAvailable((Boolean) data.get("available"));
                location.setColor((String) data.get("color"));
                location.isCity = (Boolean) data.get("isCity");

                if (!location.getAvailable()) {
                    if (location.isCity) {
                        location.promoteToCity(location.getColor());
                    } else {
                        location.buildOnLocation(location.getColor());
                    }
                }
            }

            for (int x = 0; x < roadLocations.size(); x++) {
                Map<String, Object> data = roadLocations.get(x);
                RoadLocation roadLocation = this.roadLocations.get(x);

                roadLocation.setAvailable((Boolean) data.get("available"));
                roadLocation.setColor((String) data.get("color"));

                if (!roadLocation.getAvailable()) {
                    roadLocation.buildRoad(roadLocation.getColor());
                }
            }
        });
    }

    /**
     * Updated the firebase with new data so the other player's can see
     * that something has been build
     *
     * @author Youp van Leeuwen
     *
     * @param batch batch of data that needs to be committed after the locations
     */
    private void updateFirebaseData(WriteBatch batch) {
        WriteBatch writeBatch = Firebase.db.batch();

        Map<String, Object> data = new HashMap<>();
        data.put("locations", locations);
        data.put("roadLocations", roadLocations);

        writeBatch.set(gameReference.collection("locations").document("data"), data);
        ApiFuture<List<WriteResult>> future =  writeBatch.commit();

        try {
            for (WriteResult result: future.get()) { // Makes sure the locations have been updated before updating the game
                batch.commit();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects the tiles with the corresponding locations numbers.
     *
     * @author Tijs Groenendaal
     *
     * @param tiles A list of all tiles on the map.
     */
    public void linkTilesToLocations(List<Tile> tiles) {
        this.tiles = tiles;
        createLinkToLocation(tiles.get(0), 1, 2, 3, 9, 10, 11);
        createLinkToLocation(tiles.get(1), 3, 4, 5, 11, 12, 13);
        createLinkToLocation(tiles.get(2), 5, 6, 7, 13, 14, 15);
        createLinkToLocation(tiles.get(3), 8, 9, 10, 18, 19, 20);
        createLinkToLocation(tiles.get(4), 10, 11, 12, 20, 21, 22);
        createLinkToLocation(tiles.get(5), 12, 13, 14, 22, 23, 24);
        createLinkToLocation(tiles.get(6), 14, 15, 16, 24, 25, 26);
        createLinkToLocation(tiles.get(7), 17, 18, 19, 28, 29, 30);
        createLinkToLocation(tiles.get(8), 19, 20, 21, 30, 31, 32);
        createLinkToLocation(tiles.get(9), 21, 22, 23, 32, 33, 34);
        createLinkToLocation(tiles.get(10), 23, 24, 25, 34, 35, 36);
        createLinkToLocation(tiles.get(11), 25, 26, 27, 36, 37, 38);
        createLinkToLocation(tiles.get(12), 29, 30, 31, 39, 40, 41);
        createLinkToLocation(tiles.get(13), 31, 32, 33, 41, 42, 43);
        createLinkToLocation(tiles.get(14), 33, 34, 35, 43, 44, 45);
        createLinkToLocation(tiles.get(15), 35, 36, 37, 45, 46, 47);
        createLinkToLocation(tiles.get(16), 40, 41, 42, 48, 49, 50);
        createLinkToLocation(tiles.get(17), 42, 43, 44, 50, 51, 52);
        createLinkToLocation(tiles.get(18), 44, 45, 46, 52, 53, 54);
    }

    /**
     * Goes through all the Tiles and connects all the Locations.
     * If the tile has a robber, set "available" of the robberLocation to false.
     *
     * @author Tijs Groenendaal
     * @author Sean Vis
     *
     * @param tile The tile to connect locations to.
     * @param locationNumbers The locations a Tile is connected to.
     */
    private void createLinkToLocation(Tile tile, int... locationNumbers) {
        if (tile.getHasRobber()) {
            robberLocations.get(tiles.indexOf(tile)).setAvailable(false);
        }
        for (int locationNumber : locationNumbers) {
            tile.addLocation(locations.get(locationNumber - 1));
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Creates a snapshot listener for the given reference and checks if
     * a player has build something and subtracts resources accordingly.
     *
     * @author Youp van Leeuwen
     * @author Tijs Groenendaal
     *
     * @param locationsReference Firebase document reference of the location
     */
    public void createEventListener(DocumentReference locationsReference) {
        locationsReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirestoreException error) {
                try {
                    if (value != null) {
                        LocationData locationData = value.toObject(LocationData.class);

                        if (locationData != null) {
                            updateLocations(locationData.locations, locationData.roadLocations);

                            if (!buildState.equals("null")) {
                                switch (buildState) {
                                    case "city": {
                                        Map<String, Integer> toUpdate = game.getPlayers().get(game.getPlayers().indexOf(player)).getInventory().getResourceCards();
                                        toUpdate.put("grain", toUpdate.get("grain") - 1);
                                        toUpdate.put("grain", toUpdate.get("grain") - 1);
                                        toUpdate.put("ore", toUpdate.get("ore") - 1);
                                        toUpdate.put("ore", toUpdate.get("ore") - 1);
                                        toUpdate.put("ore", toUpdate.get("ore") - 1);
                                        break;
                                    }
                                    case "settlement": {
                                        Map<String, Integer> toUpdate = game.getPlayers().get(game.getPlayers().indexOf(player)).getInventory().getResourceCards();
                                        toUpdate.put("lumber", toUpdate.get("lumber") - 1);
                                        toUpdate.put("brick", toUpdate.get("brick") - 1);
                                        toUpdate.put("wool", toUpdate.get("wool") - 1);
                                        toUpdate.put("grain", toUpdate.get("grain") - 1);
                                        break;
                                    }
                                    case "road": {
                                        Map<String, Integer> toUpdate = game.getPlayers().get(game.getPlayers().indexOf(player)).getInventory().getResourceCards();
                                        toUpdate.put("lumber", toUpdate.get("lumber") - 1);
                                        toUpdate.put("brick", toUpdate.get("brick") - 1);
                                        break;
                                    }
                                    case "roadCard": {
                                        showRoads();
                                        break;
                                    }
                                }
                                buildState = "null";
                                Firebase.db.collection("games").document(game.getCode()).set(game);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Function is called each time a new road is placed to setup
     * the calculation of the longest road.
     *
     * This function also gives out the longest road points
     *
     * @author Youp van Leeuwen
     */
    public void longestRoad() {
        checkedRoads = new ArrayList<>();

        for (Location location : locations) {
            if (location.getColor().equals(player.getColor())) {
                findEndOfRoad(location);
            }
        }

        player.setLongestRoad(longestRoad);

        if (longestRoad > 4) {
            boolean claimed = false;

            for (Player player : game.getPlayers()) {
                if (player.getHasLongestRoad()) {
                    claimed = true;

                    if (this.player.getLongestRoad() > player.getLongestRoad() && !player.equals(this.player)) {
                        player.setHasLongestRoad(false);
                        player.getInventory().addVictoryPoints(true, -2);

                        this.player.setHasLongestRoad(true);
                        this.player.getInventory().addVictoryPoints(true, 2);
                    }
                }
            }

            if (!claimed) {
                this.player.setHasLongestRoad(true);
                this.player.getInventory().addVictoryPoints(true, 2);
            }
        }

        gameReference.update("players", game.getPlayers());
    }

    /**
     * Recursive function to find the end of a street/road.
     * When the function finds a location that has no connecting roads with the same color of the player,
     * it calls the findLongestRoad function and starts calculating the longest road
     *
     * @author Youp van Leeuwen
     *
     * @param location The location where this functions checks if there are any roads of the same color as the players
     */
    private void findEndOfRoad(Location location) {
        boolean endReached = false;

        for (RoadLocation roadLocation : location.roadLocations) {
            if (!checkedRoads.contains(roadLocation)) {
                checkedRoads.add(roadLocation);
                if (roadLocation.getColor().equals(player.getColor())) {
                    findEndOfRoad(roadLocation.getStartLocation());
                    findEndOfRoad(roadLocation.getEndLocation());
                    endReached = false;
                } else {
                    endReached = true;
                }
            }
        }

        if (endReached) {
            findLongestRoad(location, 1, new ArrayList<>());
        }
    }

    /**
     * Recursive function to calculate the longest road. It keeps track
     * of the count by using the count variable. Infinite loops are prevented
     * by using the checkRoadLocations, which is a list of the checked roads
     *
     * @author Youp van Leeuwen
     *
     * @param location the location that needs to be check for connecting road
     * @param count the length of the road (Starts with one)
     * @param checkedRoadLocations a list of the locations that have already been checked to prevent an infinite loop
     */
    private void findLongestRoad(Location location, int count, List<RoadLocation> checkedRoadLocations) {
        for (RoadLocation roadLocation : location.roadLocations) {
            if (!checkedRoadLocations.contains(roadLocation)) {
                checkedRoadLocations.add(roadLocation);
                if (roadLocation.getColor().equals(player.getColor())) {
                    if (count > longestRoad) {
                        longestRoad = count;
                    }

                    if (location.equals(roadLocation.getStartLocation())) {
                        findLongestRoad(roadLocation.getEndLocation(), count + 1, checkedRoadLocations);
                    } else {
                        findLongestRoad(roadLocation.getStartLocation(), count + 1, checkedRoadLocations);
                    }
                }
            }
        }
    }
}
