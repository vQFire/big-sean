import Application.Firebase;
import Models.Chat;
import Models.Game;
import Models.Player;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GameModelTest {
    @Ignore
    private Game setupGame() {
        Game game = new Game();
        game.setCode("TESTGAME");
        Player player = new Player("Piet Paulusma");
        player.setHost(true);

        game.getPlayers().add(player);
        Firebase.initialiseFirebase();
        return game;
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_GenerateRandomCodeEachTime_When_ANewGameIsCreated () {
        List<String> generatedCode = new ArrayList<>();

        for (int x = 0; x < 10_000; x++) {
            Game game = new Game();
            assertFalse(generatedCode.contains(game.getCode()));
            generatedCode.add(game.getCode());
        }
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_LoopTroughNextPlayer_When_GoingToNextTurn () {
        Game game = new Game();
        Player player1 = new Player("1");
        Player player2 = new Player("2");
        Player player3 = new Player("3");
        Player player4 = new Player("4");

        Collections.addAll(game.getPlayers(), player1, player2, player3, player4);

        for (int x = 0; x < 100; x++) {
            game.nextPlayer();

            assertTrue(game.getCurrentPlayer() < game.getPlayers().size());
            assertTrue(game.getCurrentPlayer() >= 0);
        }
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_AddPlayer_When_PlayerJoins() {
        Game game = setupGame();
        game.addPlayer(new Player());

        assertEquals(2, game.getPlayers().size());
    }
}
