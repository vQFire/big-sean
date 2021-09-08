package Controllers;

import Application.SceneManager;
import Factories.MediaPlayerFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

/**
 * @author Sean Vis
 */
public class PlayerSettingsController {
    public Slider volumeSlider;
    public CheckBox soundMute;

    /**
     * Saves the sound settings.
     * If mute button in selected, volume = 0.
     * Else volume = value of the volume slider in PlayerSettingsView.fxml.
     * Returns to previous scene.
     *
     * @author Sean Vis
     */
    public void saveSettings(ActionEvent actionEvent) {
        if (soundMute.isSelected()) {
            MediaPlayerFactory.volume = 0.0;
        } else {
            MediaPlayerFactory.volume = volumeSlider.getValue();
        }

        SceneManager.backToLastScene();
    }

    /**
     * Returns to last scene
     */
    public void goBack(ActionEvent actionEvent) {
        SceneManager.backToLastScene();
    }
}
