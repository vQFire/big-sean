package Models;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Tijs Groenendaal
 */
public class Port {
    private String receive;
    private int[] connected;
    private int amount;

    /**
     * Contains all the data for a Port.
     * Is Created in {@link Controllers.GameController#initialize(URL, ResourceBundle)}
     */
    public Port(String receive, int amount, int... connected) {
        this.receive = receive;
        this.amount = amount;
        this.connected = connected;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public int[] getConnected() {
        return connected;
    }

    public void setConnected(int[] connected) {
        this.connected = connected;
    }

    public int giveAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
