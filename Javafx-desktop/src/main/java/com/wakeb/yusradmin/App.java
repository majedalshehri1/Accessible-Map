package com.wakeb.yusradmin;

import com.wakeb.yusradmin.navigation.NavigationManager;
import com.wakeb.yusradmin.navigation.SceneType;
import com.wakeb.yusradmin.utils.HostServicesSinglton;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Objects;

public class App extends Application {

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private static boolean fontsLoaded = false;

    @Override
    public void start(Stage primaryStage) {
        HostServicesSinglton.setHostServices(this.getHostServices());
        ensureFontsLoaded();

        // Splash image
        ImageView logoView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/yusr_text_black.png")))
        );
        logoView.setOpacity(0);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(400); // Adjust size as needed

        StackPane splashRoot = new StackPane(logoView);
        Scene splashScene = new Scene(splashRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
        applyFontAndCss(splashScene);
        Image image = new Image(
                Objects.requireNonNull(App.class.getResource("/images/logo-yusr.png"))
                        .toExternalForm()
        );
        primaryStage.getIcons().add(image);

        primaryStage.setScene(splashScene);
        primaryStage.setTitle("yusr Admin App");
        primaryStage.show();

        // Fade animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), logoView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);

        fadeIn.setOnFinished(e -> {
            try {
                NavigationManager navigationManager = NavigationManager.getInstance();
                navigationManager.initializeStage(primaryStage);

                primaryStage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) applyFontAndCss(newScene);
                });

                navigationManager.navigateToScene(SceneType.LOGIN);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fadeIn.play();
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

    private void applyFontAndCss(Scene scene) {
        var css = Objects.requireNonNull(
                getClass().getResource("/css/main.css")
        ).toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
        scene.getRoot().setStyle("-fx-font-family: 'Cairo';");
    }
}