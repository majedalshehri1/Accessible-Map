package com.wakeb.yusradmin.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * NavigationManager is responsible for:
 * - Handling scene navigation (switching between standalone scenes like login)
 * - Handling view navigation (switching content inside the main layout center)
 * - Managing mapping between SceneType and FXML/CSS paths
 *
 * Implements Singleton pattern (only one instance exists).
 */
public class NavigationManager {
    // Singleton instance
    private static NavigationManager navigationManager;

    // Main stage for the app
    private Stage primaryStage;

    // Reference to the root layout when using MAIN layout (BorderPane)
    private BorderPane mainLayout;

    // Maps for storing scene/view FXML & CSS paths
    private Map<SceneType, String> fxmlPaths;
    private Map<SceneType, String> cssPaths;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes FXML and CSS paths.
     */
    private NavigationManager() {
        initPaths();
    }

    /**
     * Returns the single instance of NavigationManager.
     */
    public static NavigationManager getInstance() {
        if (navigationManager == null) {
            navigationManager = new NavigationManager();
        }
        return navigationManager;
    }

    /**
     * Initialize the stage (called from App.java).
     */
    public void initializeStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Navigate to a standalone scene (like login, main, etc).
     * Replaces the entire scene of the primary stage.
     */
    public void navigateToScene(SceneType sceneType) {
        try {
            // Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPaths.get(sceneType)));
            Pane root = fxmlLoader.load();

            // Create new Scene
            Scene scene = new Scene(root);

            // Load CSS (if exists)
            String cssPath = cssPaths.get(sceneType);
            if (cssPath != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                System.out.println("Applied CSS: " + cssPath);
            } else {
                System.out.println("No CSS found for scene: " + sceneType);
            }

            // Set scene to stage
            primaryStage.setScene(scene);

            // If MAIN layout → store reference and set default center content (Dashboard)
            if (sceneType == SceneType.MAIN) {
                this.mainLayout = (BorderPane) root;
                navigateToView(SceneType.OVERVIEWS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigate within the MAIN layout only.
     * Changes the center content of the BorderPane without replacing the whole scene.
     */
    public void navigateToView(SceneType viewType) {
        if (mainLayout == null) {
            System.err.println("Main layout not initialized!");
            return;
        }

        try {
            // Load new FXML view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPaths.get(viewType)));
            Pane view = loader.load();

            // Replace center content of BorderPane
            mainLayout.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the mapping of SceneTypes → FXML and CSS paths.
     */
    private void initPaths() {
        fxmlPaths = new HashMap<>();
        cssPaths = new HashMap<>();

        // Map SceneTypes to FXML files
        fxmlPaths.put(SceneType.LOGIN, "/fxml/auth/AuthView.fxml");
        fxmlPaths.put(SceneType.MAIN, "/fxml/dashboard/DashboardView.fxml");
        fxmlPaths.put(SceneType.OVERVIEWS, "/fxml/dashboard/content/OverviewView.fxml");
        fxmlPaths.put(SceneType.USERS, "/fxml/dashboard/content/UsersView.fxml");
        fxmlPaths.put(SceneType.REVIEWS, "/fxml/dashboard/content/ReviewsView.fxml");
        fxmlPaths.put(SceneType.PLACES, "/fxml/dashboard/content/PlacesView.fxml");

        // Map SceneTypes to CSS files
        cssPaths.put(SceneType.LOGIN, "/fxml/dashboard/css/main.css");
        cssPaths.put(SceneType.MAIN, "/fxml/dashboard/css/main.css");
        cssPaths.put(SceneType.OVERVIEWS, "/fxml/dashboard/css/main.css");
        cssPaths.put(SceneType.USERS, "/fxml/dashboard/css/main.css");
        cssPaths.put(SceneType.REVIEWS, "/fxml/dashboard/css/main.css");
        cssPaths.put(SceneType.PLACES, "/fxml/dashboard/css/main.css");
    }
}
