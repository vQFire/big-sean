package Models;

import Observers.MapObservable;
import Observers.MapObserver;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.FirestoreException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tijs Groenendaal
 */
public class Tiles implements MapObservable {
    private ArrayList<Tile> tiles = new ArrayList<>();
    private List<MapObserver> observers = new ArrayList<>();

    /**
     * Empty constructor for Firebase .toObject fucntion
     */
    public Tiles() {}

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }

    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    public void registerObserver (MapObserver mapObserver) {
        observers.add(mapObserver);
    }

    /**
     * When firebase has update in map collections all the listeners are updated
     * @param mapReference reference to Firebase map collection
     */
    public void createListener(DocumentReference mapReference) {
        mapReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirestoreException e) {
                Tiles tiles = documentSnapshot.toObject(Tiles.class);
                for (MapObserver mapObserver: observers) {
                    mapObserver.update(tiles);
                }
            }
        });
    }
}
