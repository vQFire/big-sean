package Controllers;

import Application.Firebase;
import Application.SceneManager;
import Exceptions.NotEnoughDevelopmentCardsException;
import Exceptions.NotEnoughResourcesException;
import Factories.MediaPlayerFactory;
import Models.*;
import Observers.*;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteBatch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class GameController implements GameObserver, ChatObserver, MapObserver, TradeObserver, Initializable {
    public VBox ChatLog, messagesWrapper;
    public HBox makeTradeOfferWindow, acceptedTradesWindow, tradingOfferWindow,
            player_player1_inv, player_player2_inv, player_player3_inv, player_player4_inv,
            actionHolder;
    public TextField Chat_text;
    public Button accept_button, refuse_button, diceBtn;
    public ScrollPane chatScrollPane;

    public Label player_player1_name, player_player2_name, player_player3_name, player_player4_name,
            player_player1_cards, player_player2_cards, player_player3_cards, player_player4_cards,
            player_player1_dev, player_player2_dev, player_player3_dev, player_player4_dev,
            player_player1_army, player_player2_army, player_player3_army, player_player4_army,
            player_player1_road, player_player2_road, player_player3_road, player_player4_road,
            player_monopoly_amount, player_victory_amount, player_knight_amount, player_road_amount,
            player_year_amount,
            player_lumber_amount, player_brick_amount, player_wool_amount, player_grain_amount, player_ore_amount,
            player_player1_score, player_player2_score, player_player3_score, player_player4_score;

    public Polygon tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8, tile9, tile10,
            tile11, tile12, tile13, tile14, tile15, tile16, tile17, tile18, tile19;

    public ImageView tile1nmr, tile2nmr, tile3nmr, tile4nmr, tile5nmr, tile6nmr, tile7nmr, tile8nmr, tile9nmr,
            tile10nmr, tile11nmr, tile12nmr, tile13nmr, tile14nmr, tile15nmr, tile16nmr, tile17nmr, tile18nmr, tile19nmr,
            // Robber locations
            robberLoc1, robberLoc2, robberLoc3, robberLoc4, robberLoc5, robberLoc6, robberLoc7, robberLoc8,
            robberLoc9, robberLoc10, robberLoc11, robberLoc12, robberLoc13, robberLoc14, robberLoc15, robberLoc16,
            robberLoc17, robberLoc18, robberLoc19,
            // Building locations
            buildingLoc1, buildingLoc2, buildingLoc3, buildingLoc4, buildingLoc5, buildingLoc6, buildingLoc7,
            buildingLoc8, buildingLoc9, buildingLoc10, buildingLoc11, buildingLoc12, buildingLoc13, buildingLoc14,
            buildingLoc15, buildingLoc16, buildingLoc17, buildingLoc18, buildingLoc19, buildingLoc20, buildingLoc21,
            buildingLoc22, buildingLoc23, buildingLoc24, buildingLoc25, buildingLoc26, buildingLoc27, buildingLoc28,
            buildingLoc29, buildingLoc30, buildingLoc31, buildingLoc32, buildingLoc33, buildingLoc34, buildingLoc35,
            buildingLoc36, buildingLoc37, buildingLoc38, buildingLoc39, buildingLoc40, buildingLoc41, buildingLoc42,
            buildingLoc43, buildingLoc44, buildingLoc45, buildingLoc46, buildingLoc47, buildingLoc48, buildingLoc49,
            buildingLoc50, buildingLoc51, buildingLoc52, buildingLoc53, buildingLoc54,
            // Roads
            road1, road2, road3, road4, road5, road6, road7, road8, road9, road10, road11, road12, road13, road14,
            road15, road16, road17, road18, road19, road20, road21, road22, road23, road24, road25, road26, road27, road28,
            road29, road30, road31, road32, road33, road34, road35, road36, road37, road38, road40, road41, road42, road43,
            road44, road45, road46, road47, road48, road49, road50, road51, road52, road53, road54, road55, road56, road57,
            road58, road59, road60, road61, road62, road63, road64, road65, road66, road67, road68, road69, road70, road71,
            road72, road73,
            // Ports
            port1, port2, port3, port4, port5, port6, port7, port8, port9, bank_trade,
            // Dice
            dice1, dice2,
            // Trading
            trade_accept_player1, trade_accept_player2, trade_accept_player3, trade_icon_player1,
            trade_icon_player2, trade_icon_player3, trade_slot_1, trade_slot_2, trade_slot_3, trade_slot_4, trade_slot_5, trade_slot_6, trade_slot_7, trade_slot_8,
            requested_Resource1, requested_Resource2, requested_Resource3, requested_Resource4,
            offered_Resource1, offered_Resource2, offered_Resource3, offered_Resource4;

    private HBox[] playerInvs;
    private Label[] playerNames, playerCards, playerDevCards, playerArmies, playerRoad, playerScores;
    private Polygon[] tilesPoly;
    private Tiles tiles;
    private Port[] ports;
    public ImageView[] portLocations, tileNmr, robberLocations, tradeSlots, requestedResources,
            offeredResources, responseIcons, responsePlayerIcons, buildingLocations, roadLocations;

    private Chat chat;
    private Game game;
    private Trade trade;
    /**
     * The local player instance
     */
    public Player player;
    public Inventory devCardToPlay;
    public boolean canThrow, tilesMade, startPhase = false;
    private LocationsController locationsController;
    private DocumentReference gameReference;
    private MapController mapController;
    private VictoryController victoryController;
    private TradeController tradeController;
    private ActionController actionController;

    /**
     * Runs when the GameView is loaded into the FXMLLoader in SceneManager
     * Places all FXML variables in Arrays or Lists for easy access with indexes
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerInvs = new HBox[]{
                player_player1_inv, player_player2_inv, player_player3_inv, player_player4_inv
        };

        playerNames = new Label[]{
                player_player1_name, player_player2_name, player_player3_name, player_player4_name
        };

        playerCards = new Label[]{
                player_player1_cards, player_player2_cards, player_player3_cards, player_player4_cards
        };

        playerDevCards = new Label[]{
                player_player1_dev, player_player2_dev, player_player3_dev, player_player4_dev
        };

        playerArmies = new Label[]{
                player_player1_army, player_player2_army, player_player3_army, player_player4_army
        };

        playerRoad = new Label[]{player_player1_road, player_player2_road, player_player3_road, player_player4_road};

        buildingLocations = new ImageView[]{
                buildingLoc1, buildingLoc2, buildingLoc3, buildingLoc4, buildingLoc5, buildingLoc6, buildingLoc7,
                buildingLoc8, buildingLoc9, buildingLoc10, buildingLoc11, buildingLoc12, buildingLoc13, buildingLoc14,
                buildingLoc15, buildingLoc16, buildingLoc17, buildingLoc18, buildingLoc19, buildingLoc20, buildingLoc21, buildingLoc22,
                buildingLoc23, buildingLoc24, buildingLoc25, buildingLoc26, buildingLoc27, buildingLoc28, buildingLoc29,
                buildingLoc30, buildingLoc31, buildingLoc32, buildingLoc33, buildingLoc34, buildingLoc35, buildingLoc36,
                buildingLoc37, buildingLoc38, buildingLoc39, buildingLoc40, buildingLoc41, buildingLoc42, buildingLoc43,
                buildingLoc44, buildingLoc45, buildingLoc46, buildingLoc47, buildingLoc48, buildingLoc49, buildingLoc50,
                buildingLoc51, buildingLoc52, buildingLoc53, buildingLoc54
        };

        roadLocations = new ImageView[]{road1, road2, road3, road4, road5, road6, road7,
                road8, road9, road10, road11, road12, road13, road14,
                road15, road16, road17, road18, road19, road20, road21, road22,
                road23, road24, road25, road26, road27, road28, road29,
                road30, road31, road32, road33, road34, road35, road36,
                road37, road38, road40, road41, road42, road43,
                road44, road45, road46, road47, road48, road49, road50,
                road51, road52, road53, road54, road55, road56, road57, road58, road59, road60, road61, road62,
                road63, road64, road65, road66, road67, road68, road69, road70, road71, road72, road73};

        robberLocations = new ImageView[]{
                robberLoc1, robberLoc2, robberLoc3, robberLoc4, robberLoc5, robberLoc6, robberLoc7, robberLoc8,
                robberLoc9, robberLoc10, robberLoc11, robberLoc12, robberLoc13, robberLoc14, robberLoc15, robberLoc16,
                robberLoc17, robberLoc18, robberLoc19
        };

        portLocations = new ImageView[]{
                port1, port2, port3, port4, port5, port6, port7, port8, port9
        };

        ports = new Port[]{
                new Port(null, 3, 0, 1),
                new Port("grain", 2, 3, 4),
                new Port(null, 3, 14, 15),
                new Port("lumber", 2, 27, 38),
                new Port(null, 3, 45, 46),
                new Port("brick", 2, 50, 51),
                new Port(null, 3, 47, 48),
                new Port("wool", 2, 28, 38),
                new Port("ore", 2, 7, 17),
                new Port(null, 4, 2)
        };

        locationsController = new LocationsController(buildingLocations, roadLocations, robberLocations);

        tilesPoly = new Polygon[]{
                tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8, tile9, tile10, tile11, tile12, tile13,
                tile14, tile15, tile16, tile17, tile18, tile19
        };

        tileNmr = new ImageView[]{
                tile1nmr, tile2nmr, tile3nmr, tile4nmr, tile5nmr, tile6nmr, tile7nmr, tile8nmr, tile9nmr, tile10nmr,
                tile11nmr, tile12nmr, tile13nmr, tile14nmr, tile15nmr, tile16nmr, tile17nmr, tile18nmr, tile19nmr
        };

        tradeSlots = new ImageView[]{trade_slot_1, trade_slot_2, trade_slot_3, trade_slot_4, trade_slot_5, trade_slot_6, trade_slot_7, trade_slot_8};
        requestedResources = new ImageView[]{requested_Resource1, requested_Resource2, requested_Resource3, requested_Resource4};
        offeredResources = new ImageView[]{offered_Resource1, offered_Resource2, offered_Resource3, offered_Resource4};
        responseIcons = new ImageView[]{trade_accept_player1, trade_accept_player2, trade_accept_player3};
        responsePlayerIcons = new ImageView[]{trade_icon_player1, trade_icon_player2, trade_icon_player3};

        playerScores = new Label[]{player_player1_score, player_player2_score, player_player3_score, player_player4_score};

        for (Message message : Chat.messages) {
            addChatMessage(message);
        }

        mapController = new MapController();
        FXMLLoader loader = SceneManager.getSceneLoader("VictoryView");
        try {
            Parent root = loader.load();
            SceneManager.addScene("Victory", new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
        victoryController = loader.getController();
    }

    public ActionController getActionController() {
        return actionController;
    }

    public void trade() {
        tradeController.openTradeOfferWindow(makeTradeOfferWindow);
    }

    public void clearTradeSlot(MouseEvent mouseEvent) {
        tradeController.clearTradeSlot(mouseEvent);
    }

    public void giveResource(MouseEvent mouseEvent) {
        tradeController.giveResource(mouseEvent);
    }

    public void askResource(MouseEvent mouseEvent) {
        tradeController.askResource(mouseEvent);
    }

    public void sendTrade() {
        tradeController.sendTrade();
        makeTradeOfferWindow.setVisible(false);
    }

    public void acceptTrade() {
        CollectionReference tradeCollection = Firebase.db.collection("games").document(game.getCode()).collection("trade");
        tradeController.acceptTrade(tradeCollection, tradingOfferWindow);
    }

    public void refuseTrade() {
        CollectionReference tradeCollection = Firebase.db.collection("games").document(game.getCode()).collection("trade");
        tradeController.refuseTrade(tradeCollection, tradingOfferWindow);
    }

    public void cancelTrade(MouseEvent mouseEvent) {
        tradeController.cancelTrade(tradingOfferWindow, acceptedTradesWindow);
    }

    public void chooseTradePlayer(MouseEvent mouseEvent) {
        tradeController.chooseTradePlayer(mouseEvent);
        tradeController.cancelTrade(tradingOfferWindow, acceptedTradesWindow);
    }

    /**
     * Constructor like function, that can be called in the Lobby controller
     *
     * @param chat   the chat of the Lobby controller
     * @param game   the game created in the Lobby controller
     * @param player the local player (Can also be found in the game.getPLayers())
     */
    public void setupGame(Chat chat, Game game, Player player) {
        setChat(chat);
        this.player = player;
        setGame(game);
        setCanThrow(game.getCurrentPlayer());
        showPlayers();
        tiles = new Tiles();
        startPhase();
        devCardToPlay = player.getInventory();

        WriteBatch batch = Firebase.db.batch();

        DocumentReference locationsReference = gameReference.collection("locations").document("data");
        locationsController.createEventListener(locationsReference);
        locationsController.setGameReference(gameReference);

        actionController = new ActionController(locationsController, chat, ports);
        actionController.setGameReference(gameReference);

        updateControllers(game, player);

        Map<String, Object> locationsData = new HashMap<>();
        locationsData.put("locations", locationsController.getLocations());
        locationsData.put("roadLocations", locationsController.getRoadLocations());
        batch.set(locationsReference, locationsData);

        DocumentReference mapReference = gameReference.collection("map").document("map");
        tiles.registerObserver(this);
        tiles.createListener(mapReference);

        tradeController = new TradeController(game.getCode(), getLocalPlayerIndex(), game);
        tradeController.addViews(tradeSlots, requestedResources, offeredResources, responseIcons, responsePlayerIcons, accept_button, refuse_button);
        tradeController.setTradeRef(Firebase.db.collection("games").document(game.getCode()).collection("trade").document("tradeRequests"));
        trade = tradeController.trade;

        batch.set(gameReference.collection("trade").document("tradeRequests"), tradeController.trade.tradeStatus);

        if (player.getHost()) {
            batch.set(mapReference, createMap());
        }

        batch.commit();

        tradeController.trade.registerObserver(this);
    }

    public Polygon[] getTilePolies () {
        return tilesPoly;
    }

    private boolean isPlayersTurn () {
        return getLocalPlayerIndex() == game.getCurrentPlayer();
    }

    private int getLocalPlayerIndex() {
        return game.getPlayers().indexOf(player);
    }

    private void setChat(Chat chat) {
        this.chat = chat;
        chat.registerObserver(this);
    }

    private void setGame(Game game) {
        this.game = game;
        game.registerObserver(this);

        gameReference = Firebase.db.collection("games").document(game.getCode());
        locationsController.gameReference = gameReference;
    }

    public TradeController getTradeController() {
        return tradeController;
    }

    public void setTradeController(TradeController tradeController) {
        this.tradeController = tradeController;
    }

    /**
     * Adds victory points.
     * Use True for points that are shown to all players.
     * Use False for points that are not shown to the other players.
     *
     * @author Sean Vis
     *
     * @param visible True = visible, False = hidden.
     * @param amount  amount of points to be gained.
     */
    private void addVictoryPoints(boolean visible, Integer amount) {
        player.getInventory().addVictoryPoints(visible, amount);

        if (player.getInventory().totalVictoryPoints() >= game.getSettings().get("VictoryPoints")) {
            win();
        }
    }

    /**
     * Starts win sequence.
     * Shows alert to every player, detailing if they won or lost.
     *
     * @author Sean Vis
     */
    private void win() {
        Alert alert;

        if (this.player.getInventory().totalVictoryPoints() >= game.getSettings().get("VictoryPoints")) {
            MediaPlayerFactory.playSound("victory.mp3");

            alert = new Alert(Alert.AlertType.INFORMATION, "You won!");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    openVictoryScreen();

                    try {
                        Thread.sleep(3000);
                        gameReference.delete();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION, "You lost!");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    openVictoryScreen();
                }
            });
        }
    }

    /**
     * Executes when a Firebase Snapshot has been received
     * Makes a new Box with the received text,
     *
     * @param message The instance with all data.
     */
    private void addChatMessage(Message message) {
        Platform.runLater(() -> {
            HBox messageWrap = new HBox();
            messageWrap.setSpacing(10);
            messageWrap.getChildren().addAll(
                    new Label(message.player),
                    new Label(message.formatTimestamp()),
                    new Label(message.message)
            );

            messagesWrapper.getChildren().add(messageWrap);
        });

        chatScrollPane.setVvalue(1.0);
    }

    private void checkSeven(Dice dice) {
        if (dice.diceOne + dice.diceTwo == 7) {
            int totalCards = player.getInventory().totalResources();
            if (totalCards > 7) {
                int cardsToPut = totalCards / 2;

                FXMLLoader loader = SceneManager.getSceneLoader("PutAwayView");
                try {
                    Parent root = loader.load();
                    PutAwayController putAwayController = loader.getController();
                    putAwayController.setGame(game, getLocalPlayerIndex());
                    putAwayController.setAmount(cardsToPut);

                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Put away cards");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException ignored) {
                }
            }
            actionController.robber();
        }
    }

    /**
     * Starts the beginning phase of a catan game.
     * Every player can place 2 settlements and 2 roads for free.
     * Player order goes: 0-1-2-3-3-2-1-0.
     * When the 2nd free settlement is placed, the player gets the resources of all the tiles surrounding the last settlement.
     *
     * @author Youp van Leeuwen
     * @author Sean Vis
     */
    public void startPhase() {
        startPhase = true;
        int victoryPoints = player.getInventory().totalVictoryPoints();
        actionHolder.setVisible(false);
        diceBtn.setDisable(true);

        if (isPlayersTurn()) {
            if (victoryPoints == 1) {
                if (game.getCurrentPlayer() - 1 < 0) {
                    locationsController.nextPlayer = game.getCurrentPlayer();
                } else {
                    locationsController.nextPlayer = game.getCurrentPlayer() - 1;
                }
            } else {
                if (game.getPlayers().size() - 1 < game.getCurrentPlayer() + 1) {
                    locationsController.nextPlayer = game.getCurrentPlayer();
                } else {
                    locationsController.nextPlayer = game.getCurrentPlayer() + 1;
                }
            }

            locationsController.showFreeSettlement();
        }
    }

    /**
     * Update the Image of the Dice when player has thrown the dices
     */
    private void updateDice () {
        String dice1URL = String.format("/images/dice/dice_%s.png", game.getDice().getDiceOne());
        String dice2URL = String.format("/images/dice/dice_%s.png", game.getDice().getDiceTwo());
        MediaPlayerFactory.playSound("dice_roll.mp3");
        dice1.setImage(new Image(dice1URL));
        dice2.setImage(new Image(dice2URL));
    }

    /**
     * When player N show corresponding FXML VBox on screen
     */
    private void showPlayers() {
        for (int x = 0; x < playerNames.length; x++) {
            try {
                Player player = game.getPlayers().get(x);
                String playerScore = String.format(" - %s", player.getInventory().getVictoryPoints().get("visiblePoints"));

                if (x == getLocalPlayerIndex()) {
                    playerScore = String.format(" - %s (%s)", player.getInventory().getVictoryPoints().get("visiblePoints"), player.getInventory().getVictoryPoints().get("hiddenPoints"));
                    playerScores[x].setMinWidth(35);
                } else {
                    playerScores[x].setMinWidth(20);
                }

                playerNames[x].setText(player.getName());
                playerScores[x].setText(playerScore);
                playerCards[x].setText(String.valueOf(player.getInventory().totalResources()));
                playerDevCards[x].setText(String.valueOf(player.getInventory().totalDevCards()));
                playerRoad[x].setText(String.valueOf(player.getLongestRoad()));
                playerArmies[x].setText(String.valueOf(player.getTotalKnights()));
            } catch (IndexOutOfBoundsException e) {
                playerInvs[x].setVisible(false);
            }
        }

        Map<String, Integer> resourceCards = player.getInventory().getResourceCards();
        Map<String, Integer> developmentCards = player.getInventory().getDevelopmentCards();

        player_lumber_amount.setText(String.valueOf(resourceCards.get("lumber")));
        player_wool_amount.setText(String.valueOf(resourceCards.get("wool")));
        player_brick_amount.setText(String.valueOf(resourceCards.get("brick")));
        player_grain_amount.setText(String.valueOf(resourceCards.get("grain")));
        player_ore_amount.setText(String.valueOf(resourceCards.get("ore")));

        player_monopoly_amount.setText(String.valueOf(developmentCards.get("monopoly")));
        player_knight_amount.setText(String.valueOf(developmentCards.get("knight")));
        player_victory_amount.setText(String.valueOf(developmentCards.get("victory")));
        player_road_amount.setText(String.valueOf(developmentCards.get("road")));
        player_year_amount.setText(String.valueOf(developmentCards.get("year")));
    }

    private void setCanThrow(int currentPlayer) {
        canThrow = currentPlayer == getLocalPlayerIndex();
    }

    /**
     * Generates a random map
     *
     * @author Tijs Groenendaal
     */
    private Tiles createMap() {
        tiles = mapController.createMap();

        return tiles;
    }

    //** FXML EVENTS **//

    /**
     * Executes when the player sends a chat
     */
    public void SendChat() {
        if (Chat_text.getText().length() > 0) {
            Message message = new Message(player.getName(), Chat_text.getText());
            chat.sendMessage(message);
            Chat_text.clear();
        }
    }

    /**
     * Executes when player clicks settings Button
     * Sets the current scene to the settings scene
     */
    public void openSettings() {
        SceneManager.setShowingScene("Settings");
    }

    /**
     * Executes when player clicks info Button
     * Sets the current scene to the info scene
     */
    public void openInfo() {
        SceneManager.setShowingScene("Info");
    }

    /**
     * Opens the victory screen.
     * Deletes the game document in FireStore.
     *
     * @author Sean Vis
     */
    public void openVictoryScreen() {
        for (Player player: game.getPlayers()) {
            if (player.getInventory().totalVictoryPoints() >= game.getSettings().get("VictoryPoints")) {
                victoryController.setMessage(player.getName());
                SceneManager.setShowingScene("Victory");
            }
        }
    }

    /**
     * Executes when player clicks passTurn Button
     * Gives the next player the ability to play
     *
     * @author
     */
    public void passTurn() {
        actionController.passTurn();
        actionHolder.setVisible(false);
        diceBtn.setDisable(true);
    }

    /**
     * When the settlement button is clicked, the player gets
     * shown all the possible settlement locations
     */
    public void buildSettlement() throws NotEnoughResourcesException {
        actionController.buildSettlement();
    }

    /**
     * When the city button is clicked, the player gets
     * shown all possible city locations
     */
    public void buildCity() throws NotEnoughResourcesException {
        actionController.buildCity();
    }

    /**
     * When the 'road' button is clicked, the players gets shown
     * all possible road locations
     */
    public void buildRoad() throws NotEnoughResourcesException {
        actionController.buildRoad();
    }

    /**
     * When it is the player's turn and the dice hasn't been thrown yet it generates a new dice.
     *
     * @author Ion Middelraad
     */
    public void throwDice() {
        if (isPlayersTurn() && canThrow) {
            canThrow = false;
            actionController.throwDice();
            actionHolder.setVisible(true);
            diceBtn.setDisable(true);
        }
    }

    /**
     * Allows the player to buy a random development card.
     * Subtracts the associated resources of the players inventory than adds a random
     * development card to the inventory
     *
     * @author Tijs Groenendaal
     *
     * @throws NotEnoughResourcesException when the player does not have enough resources
     */
    public void buyDevCard() throws NotEnoughResourcesException {
        if (isPlayersTurn()) {
            actionController.buyDevCard();
        }
    }

    /**
     * Checks if the player has a settlement near the port when this port is clicked by the player,
     * calls the openPortTrade function if the check is cleared
     *
     * @param mouseEvent Contains the source of the Event
     * @author Ion Middelraad and Tijs Groenendaal
     */
    public void startPortTrade(MouseEvent mouseEvent) {
        if (isPlayersTurn()) {
            ImageView imageView = (ImageView) mouseEvent.getSource();
            String fxid = imageView.getId();
            int id = Integer.parseInt(fxid.substring(4));

            for (int x : ports[id - 1].getConnected()) {
                if (locationsController.getLocations().get(x).getColor().equals(player.getColor())) {
                    actionController.openPortTrade(id);
                    break;
                }
            }
        }
    }

    /**
     * calls the openPortTrade function with the special bank id when the player clicks this button
     *
     * @author Ion Middelraad
     */
    public void startBankTrade() {
        actionController.openPortTrade(10);
    }

    //** DEVELOPMENT CARDS **//

    /**
     * Allows the player to play the monopoly card.
     * Opens a new stage where the player can select a resource to steal from all players
     *
     * @author Tijs Groenendaal
     *
     * @throws NotEnoughDevelopmentCardsException when the player doesn't have this card
     */
    public void playMonopolyCard() throws NotEnoughDevelopmentCardsException {
        if (isPlayersTurn()) {
            actionController.playMonopolyCard();
        }
    }

    /**
     * Allows the player to get 2 free resources.
     * Opens a new scene where to player can choose 2 resources
     *
     * @author Tijs Groenendaal
     *
     * @throws NotEnoughDevelopmentCardsException when the player does not have this card
     */
    public void playYearOfPlentyCard() throws NotEnoughDevelopmentCardsException {
        if (isPlayersTurn()) {
            actionController.playYearOfPlentyCard();
        }
    }

    /**
     * Allows the player to build 2 free roads.
     *
     * @author Tijs Groenendaal
     *
     * @throws NotEnoughDevelopmentCardsException when the player doesn't have this card
     */
    public void playRoadsCard() throws NotEnoughDevelopmentCardsException {
        if (isPlayersTurn()) {
            actionController.playRoadsCard();
        }
    }

    /**
     * Let's the player play a knight card.
     * When played a player can move the robber, block a tile and steal a resource.
     * When the players has the biggest knightPower he/she receives 2 points.
     * Those points are lost when another player has the biggest knightPower
     *
     * @author Youp van Leeuwen
     *
     * @throws NotEnoughDevelopmentCardsException when the player does not have this card
     */
    public void playKnightCard() throws NotEnoughDevelopmentCardsException {
        if (isPlayersTurn()) {
            actionController.playKnightCard();
        }
    }


    /**
     * When the player presses Enter when the chat TextField is active an the message will be send.
     *
     * @param keyEvent Contains the source of the Event
     */
    public void chatEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            SendChat();
        }
    }

    private void updateControllers (Game game, Player player) {
        locationsController.setGame(game);
        actionController.setGame(game);

        locationsController.setPlayer(player);
        actionController.setPlayer(player);
    }

    public Tiles getTiles() {
        return tiles;
    }

    //** OBSERVERS **//

    /**
     * Called when the trade collection within the game is updated
     *
     * @param tradeObservable contains the trade data
     */
    @Override
    public void update(TradeObservable tradeObservable) {
        Map<String, ArrayList<String>> tradeRequest = tradeObservable.getTradeStatus();
        tradeController.trade.tradeStatus = tradeRequest;

        if (tradeRequest.get("wants").size() > 0) {
            if (!tradeRequest.get("refused").contains(String.valueOf(getLocalPlayerIndex()))) {
                tradeController.showTradeRequest(tradeRequest, acceptedTradesWindow);
                tradeController.updateResponses(tradeRequest);
                tradingOfferWindow.setVisible(true);
            } else {
                tradingOfferWindow.setVisible(false);
            }
        } else {
            tradingOfferWindow.setVisible(false);
            acceptedTradesWindow.setVisible(false);
        }
    }

    /**
     * Called the map collection within this game is updated.
     *
     * @param mapObservable contains the new map data, map types and numbers
     */
    @Override
    public void update(MapObservable mapObservable) {
        tiles.setTiles(mapObservable.getTiles());

        Platform.runLater(() -> {
            if (!tilesMade) {
                mapController.initTiles(tiles.getTiles(), robberLocations, tilesPoly, tileNmr);
                locationsController.linkTilesToLocations(tiles.getTiles());
                tilesMade = true;
            }

            if (actionController.usingRoadCard) {
                locationsController.showRoads();
                actionController.usingRoadCard = false;
            }

            for (Tile tile : tiles.getTiles()) {
                if (tile.getHasRobber()) {
                    RobberLocation robberLocation = locationsController.getRobberLocations().get(tiles.getTiles().indexOf(tile));
                    robberLocation.placeRobber(robberLocation, locationsController.getRobberLocations());
                }
            }
        });
    }

    /**
     * Called when something updates in the game document
     *
     * @param gameObservable contains the new data for the game
     */
    @Override
    public void update(GameObservable gameObservable) {
        player = gameObservable.getPlayers().get(getLocalPlayerIndex());
        game.setPlayers(gameObservable.getPlayers());
        updateControllers(game, player);

        Platform.runLater(() -> {
            for (Player gamePlayer: game.getPlayers()) {
                if (gamePlayer.getInventory().totalVictoryPoints() >= game.getSettings().get("VictoryPoints")) {
                    win();
                }
            }

            if (game.getTurn() != gameObservable.getTurn()) {
                game.setTurn(gameObservable.getTurn());
                game.setCurrentPlayer(gameObservable.getCurrentPlayer());

                for (Player gamePlayer : game.getPlayers()) {
                    if (gamePlayer.getInventory().totalVictoryPoints() < 2) {
                        startPhase();
                        break;
                    } else {
                        startPhase = false;
                    }
                }

                if (getLocalPlayerIndex() == game.getCurrentPlayer()) {
                    canThrow = true;
                    actionController.setDevCardToPlay(player.getInventory());
                    diceBtn.setDisable(false);

                    if (!startPhase) {
                        MediaPlayerFactory.playSound("bell.mp3");
                    }
                }
            }

            if (gameObservable.getDice().turn != game.getDice().turn) {
                game.setDice(gameObservable.getDice());
                updateDice();
                checkSeven(gameObservable.getDice());
            }

            showPlayers();
        });
    }

    /**
     * For testing knightcards outside the action controller
     */
    public void useKnightCard() {
        actionController.useKnightCard();
    }

    /**
     * Updates the chat with new messages when an update is received from Firebase
     *
     * @param chatObservable contains the new data
     */
    @Override
    public void update(ChatObservable chatObservable) {
        addChatMessage(chatObservable.getLastMessage());
    }
}
