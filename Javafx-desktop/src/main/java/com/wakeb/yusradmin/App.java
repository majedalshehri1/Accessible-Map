package com.wakeb.yusradmin;

import com.wakeb.yusradmin.navigation.NavigationManager;
import com.wakeb.yusradmin.navigation.SceneType;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the JavaFX application.
 * Extends the Application class and sets up the primary stage.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Get singleton instance of NavigationManager
            NavigationManager navigationManager = NavigationManager.getInstance();

            navigationManager.initializeStage(primaryStage);

            navigationManager.navigateToScene(SceneType.LOGIN);

            primaryStage.setTitle("yusr Admin App");

            primaryStage.show();

        } catch (Exception e) {
            // Print any unexpected errors during startup
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
