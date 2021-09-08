package Models;

import com.google.cloud.firestore.annotation.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public String message;
    public String player;
    public Date timestamp;

    public Message() {};

    /**
     * Message that will be send by 'System'
     *
     * @param message content of the message
     */
    public Message(String message) {
        player = "System";
        timestamp = new Date();
        this.message = message;
    }

    /**
     * @param player name of the player
     * @param message content of the message
     */
    public Message(String player, String message) {
        this.message = message;
        this.player = player;
        this.timestamp = new Date();
    }

    public String formatTimestamp () {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(timestamp);
    }
}
