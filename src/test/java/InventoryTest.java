import Models.Game;
import Models.Player;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class InventoryTest {
    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_UpdateInventory_When_ResourceIsUpdated_InInventory() {
        Player player = new Player("Geert");
        Map<String, Integer> toUpdate = new HashMap<>();
        toUpdate.put("wool", 3);
        toUpdate.put("grain", 1);

        player.getInventory().removeAddResourceFromInventory(toUpdate);
        assertEquals(4, player.getInventory().totalResources());
    }

    /**
     * @author Tijs Groenendaal
     */
    @Test
    public void Should_UpdateInventory_When_DevelopmentCardsIsUpdated_InInventory() {
        Player player = new Player("Geert");
        player.getInventory().addDevelopmentCard("monopoly", 2);

        assertEquals(2, player.getInventory().totalDevCards());
    }
}
