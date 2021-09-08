package Factories;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.annotation.Nullable;
import java.io.File;
import java.net.MalformedURLException;

/**
 * @author Sean Vis
 */
public class MediaPlayerFactory {
    public static double volume = 0.5;

    /**
     * Makes and returns a new MediaPlayer.
     *
     * @author Sean Vis
     *
     * @param mediaFile Name of the media file to player, "MEDIAFILE.mp3".
     * @return returns new MediaPlayer.
     */
    public static MediaPlayer getMediaPlayer(String mediaFile) {
        File mp3File = new File("sounds/" + mediaFile);
        Media media = null;
        try {
            media = new Media(mp3File.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (media != null) {
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            return mediaPlayer;
        }

        return null;
    }

    /**
     * Plays an audio file.
     * Disposes all resources when the sound ends.
     *
     * @author Sean Vis
     *
     * @param mediaFile Name of the media file to play, "MEDIAFILE.mp3".
     */
    public static void playSound(String mediaFile){
        MediaPlayer mediaPlayer = getMediaPlayer(mediaFile);
        if (mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());
            mediaPlayer.setOnStopped(() -> mediaPlayer.dispose());
            mediaPlayer.play();
        }
    }
}
