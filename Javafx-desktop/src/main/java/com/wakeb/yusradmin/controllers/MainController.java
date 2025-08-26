package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.navigation.NavigationManager;
import com.wakeb.yusradmin.navigation.SceneType;
import com.wakeb.yusradmin.services.AuthService;
import com.wakeb.yusradmin.models.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

/**
 * MainController
 *
 * - Owns the app shell (sidebar + content area) after login.
 * - Reads session from AuthService and injects user info (name/email/avatar).
 * - Handles sidebar navigation to center views.
 * - Handles Logout (clear session + navigate back to LOGIN).
 */
public class MainController {

    // Header labels (inside sidebar) for user info
    @FXML private Label displayNameLabel;  // user display name
    @FXML private Label emailLabel;        // user email
    @FXML private Label avatarLabel;       // first letter avatar

    // Sidebar ToggleButton
    @FXML private ToggleButton overviewBtn;
    @FXML private ToggleButton usersBtn;
    @FXML private ToggleButton reviewsBtn;
    @FXML private ToggleButton placesBtn;

    // Logout button
    @FXML private Button logoutButton;

    // Services
    private NavigationManager navigationManager;
    private AuthService auth; // singleton

    // Lifecycle
    /**
     * Called by JavaFX after FXML is loaded:
     * - Grab singletons.
     * - Populate header with current user if session is valid.
     */
    @FXML
    private void initialize() {
        navigationManager = NavigationManager.getInstance();
        auth = AuthService.getInstance();
        populateUserHeader();
        // Optionally: open default center view here (e.g., Overview)
        // navigationManager.navigateToView(SceneType.OVERVIEWS);
    }

    // Add user info in sidebar header
    /**
     * Fill name/email/avatar from current session (username + email only).
     * Falls back to placeholders if no valid session.
     */
    private void populateUserHeader() {
        User u = auth.getCurrentUser();
        if (u == null) {
            setSafeText(displayNameLabel, "—");
            setSafeText(emailLabel, "");
            setSafeText(avatarLabel, "؟");
            return;
        }

        String name  = safe(u.getUserName(), "—");
        String email = safe(u.getUserEmail(), "");
        setSafeText(displayNameLabel, name);
        setSafeText(emailLabel, email);

        // Avatar = first letter from username (then email) if available
        String source = firstNonEmpty(u.getUserName(), u.getUserEmail());
        String letter = (source != null && !source.isBlank())
                ? source.trim().substring(0, 1)
                : "؟";
        setSafeText(avatarLabel, letter);
    }

    // Sidebar navigation handlers to center views
    @FXML private void handleOverviews() { navigationManager.navigateToView(SceneType.OVERVIEWS); }
    @FXML private void handleUsers()     { navigationManager.navigateToView(SceneType.USERS); }
    @FXML private void handleReviews()   { navigationManager.navigateToView(SceneType.REVIEWS); }
    @FXML private void handlePlaces()    { navigationManager.navigateToView(SceneType.PLACES); }
    @FXML private void handleMapView() {navigationManager.navigateToView(SceneType.MAP);}

    // Logout handler - async clear session + navigate to LOGIN
    /**
     * Clear session (async) and navigate to LOGIN regardless of server outcome.
     * Button is temporarily disabled to prevent double-clicks.
     */
    @FXML
    private void handleLogout() {
        if (logoutButton != null) logoutButton.setDisable(true);

        Task<Void> logoutTask = auth.logoutAsync();
        logoutTask.setOnSucceeded(e -> {
            if (logoutButton != null) logoutButton.setDisable(false);
            navigationManager.navigateToScene(SceneType.LOGIN);
        });
        logoutTask.setOnFailed(e -> {
            if (logoutButton != null) logoutButton.setDisable(false);
            navigationManager.navigateToScene(SceneType.LOGIN);
        });

        new Thread(logoutTask, "logout-thread").start();
    }

    // Utility methods
    private static void setSafeText(Label label, String text) {
        if (label != null) label.setText(text == null ? "" : text);
    }

    private static String safe(String primary, String fallback) {
        return (primary != null && !primary.isBlank()) ? primary : fallback;
    }

    private static String firstNonEmpty(String... values) {
        if (values == null) return null;
        for (String v : values) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
