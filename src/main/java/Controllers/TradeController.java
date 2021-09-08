package Controllers;

import Application.Firebase;
import Models.Game;
import Models.Player;
import Models.Trade;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteBatch;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.*;
import java.util.stream.Collectors;

public class TradeController {
    public Trade trade;
    private Map<String, ArrayList<String>> newTrade = new HashMap<>();
    private int localPlayer;
    private Game game;
    private DocumentReference tradeRef;

    public TradeController(String gameCode, int localPlayer, Game game) {
        initialiseNewTrade();

        this.localPlayer = localPlayer;
        this.trade = new Trade(gameCode);
        this.game = game;
        trade.tradeStatus = new HashMap<>(newTrade);
    }

    public void setTradeRef (DocumentReference documentReference) {
        this.tradeRef = documentReference;
    }

    /**
     * Connects variables with their respective imageViews in the view
     * @param tradeSlots the slots in which resources sit for trading
     * @param requestedResources    the resources the player wants
     * @param offeredResources  the resources the player gives
     * @param responseIcons the icons that show who accepted the trade
     * @param responsePlayerIcons   the color icons of the players who responded to the trade
     * @param accept_button the accept trade button
     * @param refuse_button the refuse trade button
     *
     * @author Ion Middelraad
     */
    public void addViews(ImageView[] tradeSlots, ImageView[] requestedResources, ImageView[] offeredResources, ImageView[] responseIcons, ImageView[] responsePlayerIcons, Button accept_button, Button refuse_button) {
        trade.tradeSlots = tradeSlots;
        trade.requestedResources = requestedResources;
        trade.offeredResources = offeredResources;
        trade.accept_button = accept_button;
        trade.refuse_button = refuse_button;
        trade.responseIcons = responseIcons;
        trade.responsePlayerIcons = responsePlayerIcons;
    }

    /**
     * Makes a new trade that can be customized and eventually send to all players
     *
     * @author Ion Middelraad
     */
    private void initialiseNewTrade() {
        newTrade.put("gives", new ArrayList<String>());
        newTrade.put("wants", new ArrayList<String>());
        newTrade.put("accepted", new ArrayList<String>());
        newTrade.put("refused", new ArrayList<String>());
        newTrade.put("tradeSender", new ArrayList<String>());
    }

    /**
     * Opens and closes the window in which the player can make a trade.
     * @param makeTradeOfferWindow the window in which the player can customize his trade
     * @author Ion Middelraad
     */
    public void openTradeOfferWindow(HBox makeTradeOfferWindow){
        makeTradeOfferWindow.setVisible(!makeTradeOfferWindow.isVisible());
        if (!makeTradeOfferWindow.isVisible()) {
            initialiseNewTrade();
        }
    }

    /**
     * Clears the selected slot in a trade and empties it
     * @param mouseEvent the source of the button clicked
     * @author Ion Middelraad
     */
    public void clearTradeSlot(MouseEvent mouseEvent) {
        ImageView id = (ImageView) mouseEvent.getSource();

        for (int i = 0; i < 8; i++) {
            if (id == trade.tradeSlots[i]) {
                ImageView tradeSlot = trade.tradeSlots[i];
                tradeSlot.setVisible(false);
                ImageView clickedResource = (ImageView) mouseEvent.getSource();
                String idString = clickedResource.getId();
                if (i > 3) {
                    newTrade.get("wants").set(i - 4, "empty");
                } else {
                    newTrade.get("gives").set(i, "empty");
                }
                break;
            }
        }
    }

    /**
     * Adds a resource to the trade that the player wants to give away
     * @param mouseEvent the source of the button clicked
     * @author Ion Middelraad
     */
    public void giveResource(MouseEvent mouseEvent) {
        Player copyPlayer = game.getPlayers().get(localPlayer);

        for (int i = 0; i < 4; i++) {
            ImageView tradeSlot = trade.tradeSlots[i];
            if (!tradeSlot.isVisible()) {
                ImageView clickedResource = (ImageView) mouseEvent.getSource();
                String idString = clickedResource.getId();
                String resource = idString.replace("give_", "");
                if (copyPlayer.getInventory().getResourceCards().get(resource) == 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "You do not have this resource!");
                    alert.showAndWait();
                    break;
                }
                String newTradeImage = String.format("/images/cards/card_%s.png", resource);
                tradeSlot.setImage(new Image(newTradeImage));
                tradeSlot.setVisible(true);

                if (newTrade.get("gives").size() < i) {
                    newTrade.get("gives").set(i, resource);
                } else {
                    newTrade.get("gives").add(resource);
                }
                break;
            }
        }
    }

    /**
     * Adds a resource to the trade that the player wants to receive
     * @param mouseEvent the source of the button clicked
     * @author Ion Middelraad
     */
    public void askResource(MouseEvent mouseEvent) {
        for (int i = 4; i < 8; i++) {
            ImageView tradeSlot = trade.tradeSlots[i];
            if (!tradeSlot.isVisible()) {
                ImageView clickedResource = (ImageView) mouseEvent.getSource();
                String idString = clickedResource.getId();
                String resource = idString.replace("ask_", "");
                String newTradeImage = String.format("/images/cards/card_%s.png", resource);
                tradeSlot.setImage(new Image(newTradeImage));
                tradeSlot.setVisible(true);

                if (newTrade.get("wants").size() < i - 4) {
                    newTrade.get("wants").set(i - 4, resource);
                } else {
                    newTrade.get("wants").add(resource);
                }
                break;
            }
        }
    }

    /**
     * Sends a trade to the Firestore database and replaces the old one
     * @author Ion Middelraad
     */
    public void sendTrade() {
        if (newTrade.get("wants").isEmpty() && newTrade.get("gives").isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "There are no resources selected!");
            alert.showAndWait();
            return;
        }
        newTrade.get("wants").removeIf(s -> s.contains("empty"));
        newTrade.get("gives").removeIf(s -> s.contains("empty"));
        newTrade.get("tradeSender").add(String.valueOf(localPlayer));

        tradeRef.set(newTrade);

        for (int i = 0; i < 8; i++) {
            ImageView tradeSlot = trade.tradeSlots[i];
            if (tradeSlot.isVisible()) {
                tradeSlot.setVisible(false);
            }
        }

        initialiseNewTrade();
    }

    /**
     * Called when the a new trade request has been put into the Firestore database
     * Shows every player the trade on the screen and depending on if they can
     * accept the trade shows the refuse and accept button
     * @param tradeRequest  the last trade request that has been send in
     * @param acceptedTradesWindow  the window that shows who accepted the trades
     * @author Ion Middelraad
     */
    public void showTradeRequest(Map<String, ArrayList<String>> tradeRequest, HBox acceptedTradesWindow) {
        if (tradeRequest.get("refused").size() >= game.getPlayers().size() - 1) {
            initialiseNewTrade();
            tradeRef.set(newTrade);
            return;
        }

        for (int i = 0; i < 4; i++) {
            trade.requestedResources[i].setVisible(false);
            trade.offeredResources[i].setVisible(false);
        }

        List<String> wants = tradeRequest.get("wants");

        for (int i = 0; i < wants.size(); i++) {
            ImageView requestedResourceSlot = trade.requestedResources[i];
            Object requestedResource = wants.get(i);
            String newTradeImage = String.format("/images/cards/card_%s.png", requestedResource);
            requestedResourceSlot.setImage(new Image(newTradeImage));
            requestedResourceSlot.setVisible(true);
        }

        List<String> gives = tradeRequest.get("gives");

        for (int i = 0; i < gives.size(); i++) {
            ImageView offeredResourceSlot = trade.offeredResources[i];
            Object offeredResource = gives.get(i);
            String newTradeImage = String.format("/images/cards/card_%s.png", offeredResource);
            offeredResourceSlot.setImage(new Image(newTradeImage));
            offeredResourceSlot.setVisible(true);
        }

        if (!tradeRequest.get("accepted").contains(String.valueOf(localPlayer)) &&
                !tradeRequest.get("refused").contains(String.valueOf(localPlayer))) {
            trade.accept_button.setVisible(true);
            trade.refuse_button.setVisible(true);
        }

        if (tradeRequest.get("tradeSender").contains(String.valueOf(localPlayer))) {
            trade.accept_button.setVisible(false);
            trade.refuse_button.setVisible(false);
            acceptedTradesWindow.setVisible(true);
        } else {
            Player copyPlayer = game.getPlayers().get(localPlayer);
            for (String resource : wants) {
                if (copyPlayer.getInventory().getResourceCards().get(resource) == 0) {
                    trade.accept_button.setVisible(false);
                    break;
                }
            }
        }
    }

    /**
     * Accept the trade and puts the player into the list of accepted players in the Firestore database
     * @param tradeCollection   the Firestore collection that contains trade requests
     * @param tradingOfferWindow    the window that shows the trade offer
     * @author Ion Middelraad
     */
    public void acceptTrade(CollectionReference tradeCollection, HBox tradingOfferWindow) {
        ArrayList<String> acceptedList = trade.tradeStatus.get("accepted");

        if (!acceptedList.contains(String.valueOf(localPlayer))) {
            acceptedList.add(String.valueOf(localPlayer));
        }

        tradeCollection.document("tradeRequests").update("accepted", acceptedList);
        trade.accept_button.setVisible(false);
        trade.refuse_button.setVisible(false);
        tradingOfferWindow.setVisible(false);
    }

    /**
     * Refuses the trade and puts the player into the list of refused players in the Firestore database
     * @param tradeCollection   the Firestore collection that contains trade requests
     * @param tradingOfferWindow    the window that shows the trade offer
     * @author Ion Middelraad
     */
    public void refuseTrade(CollectionReference tradeCollection, HBox tradingOfferWindow) {
        ArrayList<String> refusedList = trade.tradeStatus.get("refused");

        if (!refusedList.contains(String.valueOf(localPlayer))) {
            refusedList.add(String.valueOf(localPlayer));
        }

        tradeCollection.document("tradeRequests").set(trade.tradeStatus);
        trade.accept_button.setVisible(false);
        trade.refuse_button.setVisible(false);
        tradingOfferWindow.setVisible(false);
    }

    /**
     * Called when a new player is put into the "refused" or "accepted" list on the Firestore database
     * Updates the view with who accepted the trade and who refused
     * @param tradeRequest  the current trade request
     * @author Ion Middelraad
     */
    public void updateResponses(Map<String, ArrayList<String>> tradeRequest) {
        ArrayList<String> acceptedResponses = tradeRequest.get("accepted");
        Image checkmark = new Image("/images/other/checkmark.png");
        Image cross = new Image("/images/other/denied.png");

        for (int i = 0; i < 3; i++) {
            ImageView icon = trade.responseIcons[i];
            ImageView playerIcon = trade.responsePlayerIcons[i];

            icon.setVisible(false);
            playerIcon.setVisible(false);
        }

        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayers().get(i);

            if (!tradeRequest.get("tradeSender").contains(String.valueOf(i))) {
                ImageView tradeIcon = null;

                for (int j = 0; j < 3; j++) {
                    ImageView icon = trade.responseIcons[j];
                    ImageView playerIcon = trade.responsePlayerIcons[j];

                    if (!icon.isVisible()) {
                        tradeIcon = icon;
                    }

                    if (!playerIcon.isVisible()) {
                        Image newPlayerIcon = new Image(String.format("/images/player/player_bg_%s.png", player.getColor()));
                        playerIcon.setImage(newPlayerIcon);
                        playerIcon.setVisible(true);
                        break;
                    }
                }

                if (acceptedResponses.contains(String.valueOf(i))) {
                    tradeIcon.setImage(checkmark);
                } else {
                    tradeIcon.setImage(cross);
                }

                tradeIcon.setVisible(true);
            }
        }
    }

    /**
     * Called on button press, stops showing the currently ongoing trade
     * @param tradingOfferWindow the window that shows the trade offer
     * @param acceptedTradesWindow the window that shows who accepted the trade offer
     * @author Ion Middelraad
     */
    public void cancelTrade(HBox tradingOfferWindow, HBox acceptedTradesWindow) {
        initialiseNewTrade();
        Firebase.db.collection("games").document(game.getCode()).collection("trade").document("tradeRequests").set(newTrade);

        tradingOfferWindow.setVisible(false);
        acceptedTradesWindow.setVisible(false);
    }

    /**
     * Called when the player has chosen someone to trade with,
     * calls the function updatePlayerInventory with the data of the 2 trading players
     * @param mouseEvent the source of the button that got clicked
     * @author Ion Middelraad
     */
    public void chooseTradePlayer(MouseEvent mouseEvent) {
        ImageView acceptedTrade = (ImageView) mouseEvent.getSource();
        String idString = acceptedTrade.getId();
        int acceptedIndex = Integer.parseInt(idString.substring(idString.length() -1));
        ArrayList<String> tradeTempGet = trade.tradeStatus.get("wants");
        ArrayList<String> tradeTempSend = trade.tradeStatus.get("gives");
        updatePlayerInventory(tradeTempSend, tradeTempGet, acceptedIndex, trade.tradeStatus);
    }

    /**
     * Gets called by the chooseTradePlayer function,
     * trades the resources between the 2 trading players
     * @param resourcesToGive the resources the tradeSender needs to give
     * @param resourcesToGet    the resources the tradeSender needs to get
     * @param acceptedPlayerIndex the index of the player who accepted the trade
     * @author Ion Middelraad
     */
    public void updatePlayerInventory(ArrayList<String> resourcesToGive, ArrayList<String> resourcesToGet, int acceptedPlayerIndex, Map<String, ArrayList<String>> tradeStatus) {
        List<Player> playersCopy = new ArrayList<>();

        for (Player player: game.getPlayers()) {
            playersCopy.add(player.getCopy());
        }

        /* Player instance of the accepted player */
        Player acceptedPlayer = playersCopy.get(acceptedPlayerIndex);
        /* Player instance of the player that started the trade */
        Player requestedPlayer = playersCopy.get(Integer.parseInt(tradeStatus.get("tradeSender").get(0)));

        boolean notEnoughResources = false;

        for (String resource: resourcesToGive) {
            if (requestedPlayer.getInventory().getResourceCards().get(resource) > 0) {
                acceptedPlayer.getInventory().addResource(resource, 1);
                requestedPlayer.getInventory().addResource(resource, -1);
            } else {
                notEnoughResources = true;
            }
        }

        for (String resource: resourcesToGet) {
            if (acceptedPlayer.getInventory().getResourceCards().get(resource) > 0) {
                requestedPlayer.getInventory().addResource(resource, 1);
                acceptedPlayer.getInventory().addResource(resource, -1);
            } else {
                notEnoughResources = true;
            }
        }

        if (!notEnoughResources) {
            DocumentReference gameReference = Firebase.db.collection("games").document(game.getCode());
            WriteBatch batch = Firebase.db.batch();

            initialiseNewTrade();

            batch.update(gameReference, "players", playersCopy);
            batch.set(gameReference.collection("trade").document("tradeRequests"), newTrade);
            batch.commit();
        } else {
            Firebase.db.collection("games").document(game.getCode()).set(game);
        }
    }

    public Map<String, ArrayList<String>> getNewTrade() {
        return newTrade;
    }

    public void setNewTrade(Map<String, ArrayList<String>> newTrade) {
        this.newTrade = newTrade;
    }
}
