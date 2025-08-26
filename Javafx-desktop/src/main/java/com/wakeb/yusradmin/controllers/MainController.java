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
import javafx.scene.control.ToggleGroup;

/**
 * MainController
 */
public class MainController {

    // Header labels (inside sidebar) for user info
    @FXML private Label displayNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label avatarLabel;

    // Sidebar ToggleButton
    @FXML private ToggleButton overviewBtn;
    @FXML private ToggleButton usersBtn;
    @FXML private ToggleButton reviewsBtn;
    @FXML private ToggleButton placesBtn;

    // Logout button
    @FXML private Button logoutButton;

    // ToggleGroup for navigation buttons
    private ToggleGroup sideNav;

    // Services
    private NavigationManager navigationManager;
    private AuthService auth;

    @FXML
    private void initialize() {
        navigationManager = NavigationManager.getInstance();
        auth = AuthService.getInstance();

        // Create and setup ToggleGroup programmatically
        setupToggleGroup();

        populateUserHeader();
    }

    private void setupToggleGroup() {
        sideNav = new ToggleGroup();

        // Add all toggle buttons to the same group
        overviewBtn.setToggleGroup(sideNav);
        usersBtn.setToggleGroup(sideNav);
        reviewsBtn.setToggleGroup(sideNav);
        placesBtn.setToggleGroup(sideNav);

        // Select the overview button by default
        overviewBtn.setSelected(true);
    }

    // ... rest of your existing code remains the same ...
    private void populateUserHeader() {
        User u = auth.getCurrentUser();
        if (u == null) {
            setSafeText(displayNameLabel, "—");
            setSafeText(emailLabel, "");
            setSafeText(avatarLabel, "؟");
            return;
        }

        String name  = safe(u.getUsername(), "—");
        String email = safe(u.getEmail(), "");
        setSafeText(displayNameLabel, name);
        setSafeText(emailLabel, email);

        String source = firstNonEmpty(u.getUsername(), u.getEmail());
        String letter = (source != null && !source.isBlank())
                ? source.trim().substring(0, 1)
                : "؟";
        setSafeText(avatarLabel, letter);
    }

    @FXML private void handleOverviews() { navigationManager.navigateToView(SceneType.OVERVIEWS); }
    @FXML private void handleUsers()     { navigationManager.navigateToView(SceneType.USERS); }
    @FXML private void handleReviews()   { navigationManager.navigateToView(SceneType.REVIEWS); }
    @FXML private void handlePlaces()    { navigationManager.navigateToView(SceneType.PLACES); }

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