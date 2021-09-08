package Models;

import Application.Firebase;
import Observers.TradeObservable;
import Observers.TradeObserver;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.EventListener;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Ion Middelraad
 */
public class Trade implements TradeObservable {
    public ImageView[] tradeSlots , responseIcons, responsePlayerIcons;
    public ImageView[] requestedResources, offeredResources;
    public Button accept_button, refuse_button;
    public Map<String, ArrayList<String>> tradeStatus = new HashMap<>();
    private List<TradeObserver> observers = new ArrayList<>();
    private CollectionReference collectionReference;

    public Trade() {}

    /**
     * Gets called when a new trade has been put into the Firestore database
     * and updates every players view by calling the update function
     * @param gamecode
     * @author Ion Middelraad
     */
    public Trade(String gamecode) {
        collectionReference = Firebase.db.collection("games").document(gamecode).collection("trade");
        collectionReference.document("tradeRequests").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirestoreException e) {
                if (documentSnapshot != null) {
                    Trade trade = new Trade();

                    Map<String, ArrayList<String>> tradeData = new HashMap<>();
                    ArrayList<String> gives = (ArrayList<String>) documentSnapshot.get("gives");
                    ArrayList<String> wants = (ArrayList<String>) documentSnapshot.get("wants");
                    ArrayList<String> acceptedBy = (ArrayList<String>) documentSnapshot.get("accepted");
                    ArrayList<String> refusedBy = (ArrayList<String>) documentSnapshot.get("refused");
                    ArrayList<String> tradeSender = (ArrayList<String>) documentSnapshot.get("tradeSender");

                    if (gives == null || wants == null || acceptedBy == null || refusedBy == null || tradeSender == null) {
                        return;
                    }

                    tradeData.put("gives", gives);
                    tradeData.put("wants", wants);
                    tradeData.put("accepted", acceptedBy);
                    tradeData.put("refused", refusedBy);
                    tradeData.put("tradeSender", tradeSender);

                    trade.setTradeStatus(tradeData);

                    for (TradeObserver observer : observers) {
                        observer.update(trade);
                    }
                }
            }
        });
    }

    @Override
    public Map<String, ArrayList<String>> getTradeStatus() {
        return this.tradeStatus;
    }

    public void registerObserver(TradeObserver tradeObserver) {
        observers.add(tradeObserver);
    }

    public void setTradeStatus(Map<String, ArrayList<String>> tradeStatus) {
        this.tradeStatus = tradeStatus;
    }
}

