package Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the switching and loading of scenes.
 * @author Tijs Groenendaal
 * @author Youp van Leeuwen
 */
public class SceneManager {
    static private Stage window;
    static private final Map<String, Scene> SCENES = new HashMap<>();

    /**
     * The name of the last scene
     */
    static private String lastScene;

    /**
     * The name of the current scene
     */
    static private String currentScene;

    /**
     * @return FXMLLoader contains the fxml file
     */
    static public FXMLLoader getSceneLoader (String fileName) {
        return new FXMLLoader(SceneManager.class.getResource(String.format("/fxml/%s.fxml", fileName)));
    }

    static public void addScene (String fileName, String sceneName) {
        try {
            FXMLLoader loader = getSceneLoader(fileName);
            Parent root = loader.load();
            SCENES.put(sceneName, new Scene(root));
        } catch (IOException e) {
            System.out.printf("Can't load %s", fileName);

            e.printStackTrace();
        }
    }

    static public void removeScene (String name) {
        SCENES.remove(name);
    }

    static public void addScene (String sceneName, Scene scene) {
        SCENES.put(sceneName, scene);
    }

    static public void setShowingScene (String sceneName) {
        try {
            window.setScene(SCENES.get(sceneName));
            //window.centerOnScreen();
            lastScene = currentScene;
            currentScene = sceneName;

            if (!window.isShowing()) window.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.printf("%s is not a registered scene\n", sceneName);
        }
    }

    static public void backToLastScene () {
        setShowingScene(lastScene);
    }

    static public void setWindow (Stage primaryStage) {
        window = primaryStage;
    }
}
