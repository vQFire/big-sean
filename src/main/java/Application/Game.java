package Application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Game extends Application {
    @Override
    public void start(Stage primaryStage) {
//        BasicConfigurator.configure();

        Firebase.initialiseFirebase();

        SceneManager.setWindow(primaryStage);
        SceneManager.addScene("MainMenu", "Main Menu");
        SceneManager.addScene("InfoView", "Info");
        SceneManager.addScene("PlayerSettingsView", "Settings");
        SceneManager.setShowingScene("Main Menu");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
