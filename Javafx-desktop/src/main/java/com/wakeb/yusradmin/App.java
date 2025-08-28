package com.wakeb.yusradmin;

import com.wakeb.yusradmin.navigation.NavigationManager;
import com.wakeb.yusradmin.navigation.SceneType;
import com.wakeb.yusradmin.utils.HostServicesSinglton;
import javafx.application.Application;
import javafx.stage.Stage;

// Font-related imports
import javafx.scene.Scene;
import javafx.scene.text.Font;
import java.util.Objects;

/**
 * Main entry point for the JavaFX application.
 * Extends the Application class and sets up the primary stage.
 */
public class App extends Application {

    private static boolean fontsLoaded = false;

    @Override
    public void start(Stage primaryStage) {
        HostServicesSinglton.setHostServices(this.getHostServices());

        try {
            NavigationManager navigationManager = NavigationManager.getInstance();

            navigationManager.initializeStage(primaryStage);

            ensureFontsLoaded();
            primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    applyFontAndCss(newScene);
                }
            });

            navigationManager.navigateToScene(SceneType.LOGIN);

            primaryStage.setTitle("yusr Admin App");

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }



    private void ensureFontsLoaded() {
        if (fontsLoaded) return;
        Font.loadFont(Objects.requireNonNull(
                getClass().getResourceAsStream("/fonts/Cairo-Regular.ttf")
        ), 12);
        Font.loadFont(Objects.requireNonNull(
                getClass().getResourceAsStream("/fonts/Cairo-Bold.ttf")
        ), 12);
        fontsLoaded = true;
    }

    /** Applies main.css and forces Cairo font on the scene root */
    private void applyFontAndCss(Scene scene) {
        // Attach the main stylesheet (optional but useful for overall styling)
        var css = Objects.requireNonNull(
                getClass().getResource("/css/main.css")
        ).toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }

        // Force Cairo font on all scene elements
        scene.getRoot().setStyle("-fx-font-family: 'Cairo';");
    }
}
