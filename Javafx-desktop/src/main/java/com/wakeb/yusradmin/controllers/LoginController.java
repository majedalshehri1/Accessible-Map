package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.LoginRequest;
import com.wakeb.yusradmin.navigation.NavigationManager;
import com.wakeb.yusradmin.navigation.SceneType;
import com.wakeb.yusradmin.services.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * LoginController
 *
 * Responsibilities:
 * - Owns the login form UI.
 * - Handles events (Login button).
 * - Delegates authentication logic to AuthService.
 *
 * Currently:
 * - UI fields are declared.
 * - initialize() is ready to prepare state.
 * - handleLogin() is stubbed.
 */
public class LoginController {

    // UI elements (bind them in FXML with fx:id)
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;

    private AuthService authService;
    private NavigationManager navigationManager;

    @FXML
    private void initialize() {
        authService = new AuthService();
        navigationManager = NavigationManager.getInstance();

        if (loadingIndicator != null) loadingIndicator.setVisible(false);
        if (errorLabel != null) errorLabel.setVisible(false);

        // Auto-redirect if already logged in
        if (authService.isAuthenticated()) {
            navigationManager.navigateToScene(SceneType.MAIN);
        }
    }

    /**
     * Handles login button click.
     * TODO(team): Implement async login flow with AuthService.loginAsync()
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        // TODO(team): Run AuthService.loginAsync(...) and handle result
        navigationManager.navigateToScene(SceneType.MAIN); // Stub
    }

    // --- UI Helpers ---
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
