import Controllers.MainMenuController;
import javafx.scene.control.TextField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MainMenuTest extends AbstractTest {
    private MainMenuController mainMenuController = new MainMenuController();

    @Before
    public void setupMainController () {
        mainMenuController.playerName = new TextField();
        mainMenuController.InviteLink = new TextField();
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void ShouldNot_ReturnJohnDeere_When_NameIsGiven () {
        mainMenuController.playerName.setText("Youp van Leeuwen");

        mainMenuController.checkName();

        assertNotEquals("Name should not bee John Deere", "John Deere", mainMenuController.playerNameText);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_ReturnJohnDeere_When_NameIsEmpty () {
        mainMenuController.playerName.setText("");

        mainMenuController.checkName();

        assertEquals("Name should be John Deere", "John Deere", mainMenuController.playerNameText);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_ReturnTrue_When_CodeIsSevenLong () {
        mainMenuController.InviteLink.setText("ABC123A");

        boolean codeIsValid = mainMenuController.checkCode();

        assertTrue("Code is valid when it is 7 characters long", codeIsValid);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_ReturnTrue_When_CodeIsSevenLongExcludingSpaces () {
        mainMenuController.InviteLink.setText("ABC 123 A");

        boolean codeIsValid = mainMenuController.checkCode();

        assertTrue("Code is valid when it is 7 characters long excluding spaces", codeIsValid);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_ReturnFalse_When_CodeIsShorterThanSeven () {
        mainMenuController.InviteLink.setText("ABC");

        boolean codeIsValid = mainMenuController.checkCode();

        assertFalse("Code should not be valid when it's shorter than 7 characters", codeIsValid);
    }

    /**
     * @author Youp van Leeuwen
     */
    @Test
    public void Should_ReturnFalse_When_CodeIsLongerThanSeven () {
        mainMenuController.InviteLink.setText("ABCDEFG12345678");

        boolean codeIsValid = mainMenuController.checkCode();

        assertFalse("Code should not be valid when it's longer than 7 characters", codeIsValid);
    }
}
