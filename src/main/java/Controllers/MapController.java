package Controllers;

import Models.Tile;
import Models.Tiles;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MapController {

    /**
     * Generates a random map
     *
     * @author Tijs Groenendaal
     */
    public Tiles createMap () {
        Tiles tiles = new Tiles();

        Random random = new Random();

        // All possible types of an Tile
        String[] types = new String[] {
                "desert",
                "lumber", "lumber", "lumber", "lumber",
                "wool", "wool", "wool", "wool",
                "grain", "grain", "grain", "grain",
                "ore", "ore", "ore",
                "brick", "brick", "brick"
        };

        // Using Random.nextInt() doesn't feel random. Using a constant array of integers does.
        Integer[] numbers = new Integer[] {
                2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12
        };

        ArrayList<String> mapTypes = new ArrayList<>(Arrays.asList(types));
        ArrayList<Integer> numbersList = new ArrayList<>(Arrays.asList(numbers));

        for (int x = 0; x < 19; x++) {
            String type = mapTypes.get(random.nextInt(mapTypes.size()));
            Integer integer;

            Tile tile = new Tile();

            if (!type.equals("desert")) {
                integer = numbersList.get(random.nextInt(numbersList.size()));
                numbersList.remove(integer);
                tile.setHasRobber(false);
            } else {
                integer = 0;
                tile.setHasRobber(true);
            }
            tile.setNumber(integer);
            tile.setType(type);
            tiles.addTile(tile);

            mapTypes.remove(type);
        }

        return tiles;
    }

    /**
     * Makes the backgrounds for each Polygon and shows the corresponding number
     *
     * @author Tijs Groenendaal
     * @param newTiles The tiles from the snapshot
     */
    public void initTiles (ArrayList<Tile> newTiles, ImageView[] robberLocations, Polygon[] tilesPoly, ImageView[] tileNmr) {
        for (int x = 0; x < 19; x++) {
            String type = newTiles.get(x).getType();

            if (type.equals("desert")) {
                robberLocations[x].setImage(new Image("images/tiles/numbers/icon_robber.png"));
            }
            tilesPoly[x].setFill(new ImagePattern(new Image(String.format("images/tiles/tile_%s.png", type))));

            if (!newTiles.get(x).getType().equals("desert")) {
                tileNmr[x].setImage(new Image(String.format("images/tiles/numbers/prob_%s.png", newTiles.get(x).getNumber())));
            }
        }
    }
}
