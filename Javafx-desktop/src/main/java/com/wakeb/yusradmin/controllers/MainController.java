package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.navigation.NavigationManager;
import com.wakeb.yusradmin.navigation.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * MainController
 *
 * Controls the MAIN layout (shell) that contains the sidebar/topbar.
 * Only handles navigation between center views (Dashboard, Users, Settings).
 * Actual content FXMLs are loaded via NavigationManager.navigateToView(...).
 */
public class MainController {

    // Optional FXML-injected buttons (wire in FXML if needed)
    @FXML private Button btnOverviews;
    @FXML private Button btnUsers;
    @FXML private Button btnReviews;
    @FXML private Button btnPlaces;
    @FXML private Button btnLogout;

    // Singleton navigation manager
    private NavigationManager navigationManager;

    // Initialize method called by JavaFX after FXML loading
    @FXML
    private void initialize() {
        // Obtain NavigationManager instance for in-app navigation
        navigationManager = NavigationManager.getInstance();
    }

    // --- Sidebar button handlers (wired from FXML) ---

    // DASHBOARD / OVERVIEWS
    @FXML
    private void handleOverviews() {
        // Replace center content with the Dashboard view
        navigationManager.navigateToView(SceneType.OVERVIEWS);
    }

    // USERS
    @FXML
    private void handleUsers() {
        // Replace center content with the Users view
        navigationManager.navigateToView(SceneType.USERS);
    }

    // REVIEWS
    @FXML
    private void handleReviews() {
        // Replace center content with the Reviews view
        navigationManager.navigateToView(SceneType.REVIEWS);
    }

    //PLACES
    @FXML
    private void handlePlaces() {
        // Replace center content with the Reviews view
        navigationManager.navigateToView(SceneType.PLACES);
    }

    @FXML
    private void handleLogout() {
        // TODO(team): Clear session using AuthService if needed, then go back to Login
        navigationManager.navigateToScene(SceneType.LOGIN);
    }
}
