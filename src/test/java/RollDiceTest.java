import Controllers.ActionController;
import Models.Dice;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Ion Middelraad
 */
public class RollDiceTest {
    Dice dice;
    ActionController actionController;


    @Before
    public void setUpTest() {
        actionController = new ActionController();
    }

    @Test
    public void Should_RollDice_When_DiceClicked() {
        int[] rollList = new int[100];
        for (int i = 0; i < 100; i++) {
            Dice dice = actionController.rollDice();
            int dice1 = dice.getDiceOne();
            int dice2 = dice.getDiceTwo();
            int total = dice1 + dice2;
            rollList[i] = total;
        }
        double avg = average(rollList, rollList.length);
        assertTrue(avg > 6.0);
        assertTrue(avg < 8.0);
    }
    static double average(int a[], int n)
    {
        int sum = 0;

        for (int i = 0; i < n; i++)
            sum += a[i];

        return (double)sum / n;
    }
}
