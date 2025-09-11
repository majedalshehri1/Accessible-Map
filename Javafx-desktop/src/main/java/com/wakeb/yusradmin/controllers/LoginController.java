package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.LoginRequest;
import com.wakeb.yusradmin.navigation.NavigationManager;
import com.wakeb.yusradmin.navigation.SceneType;
import com.wakeb.yusradmin.services.AuthService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * LoginController
 *
 * - Owns the login screen fields and actions.
 * - Delegates authentication to AuthService (async Task).
 * - On app start, validates any stored session with the backend;
 *   if valid -> navigate to MAIN, otherwise stay on Login.
 * - On login click, navigates to MAIN only after a real success
 *   (token returned AND validated by the backend inside AuthService).
 */
public class LoginController {

    // ===== FXML fields =====
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;

    // ===== Services =====
    private AuthService auth;
    private NavigationManager navigationManager;

    @FXML
    private void initialize() {
        auth = AuthService.getInstance();
        navigationManager = NavigationManager.getInstance();

        // Initial UI state
        setLoading(false);
        if (errorLabel != null) errorLabel.setVisible(false);

        Task<Boolean> validateTask = auth.validateSessionAsync();
        validateTask.setOnSucceeded(e -> {
            boolean valid = Boolean.TRUE.equals(validateTask.getValue());
            if (valid) {
                navigationManager.navigateToScene(SceneType.MAIN);
            }
        });
        validateTask.setOnFailed(e -> {
        });
        new Thread(validateTask, "validate-session").start();
    }

    /**
     * Handle "Login" button:
     * - Validate inputs
     * - Start async login Task
     * - On success (token present & validated inside AuthService) -> MAIN
     * - On failure -> show error message
     */
    @FXML
    private void handleLogin() {
        final String email = usernameField.getText() == null ? "" : usernameField.getText().trim();
        final String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("ادخل البريد الالكتروني و كلمة المرور");
            showStyledAlert(Alert.AlertType.ERROR, "فشل تسجيل الدخول", "ادخل البريد الالكتروني و كلمة المرور");
            return;
        }
        LoginRequest request = new LoginRequest(email, password);
        Task<AuthService.LoginResult> loginTask = auth.loginAsync(request);

        setLoading(true);
        if (errorLabel != null) errorLabel.setVisible(false);

        loginTask.setOnSucceeded(e -> {
            setLoading(false);

            AuthService.LoginResult result = loginTask.getValue();

            if (result.isSuccess()) {
                navigationManager.navigateToScene(SceneType.MAIN);
            } else {
                String msg = (result.getMessage() == null || result.getMessage().isBlank())
                        ? "فشل تسجيل الدخول."
                        : result.getMessage();
                showError(msg);
                showStyledAlert(Alert.AlertType.ERROR, "فشل تسجيل الدخول", msg);
            }
        });

        loginTask.setOnFailed(e -> {
            setLoading(false);
            String msg = "Login error: " +
                    (loginTask.getException() != null ? loginTask.getException().getMessage() : "Unknown");
            showError(msg);
            showStyledAlert(Alert.AlertType.ERROR, "Login Error", msg);
        });

        // Run on a background thread
        new Thread(loginTask, "login-thread").start();
    }

    // Utility methods

    /** Toggle spinner + button state together. */
    private void setLoading(boolean loading) {
        if (loadingIndicator != null) loadingIndicator.setVisible(loading);
        if (loginButton != null) loginButton.setDisable(loading);
    }

    /** Show inline error text under the form. */
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    /** Simple alert dialog for critical messages. */
    private void showStyledAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // نجيب الـ DialogPane ونطبّق ستايل
        DialogPane pane = alert.getDialogPane();
        pane.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT); // عربي
        pane.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        pane.getStyleClass().add("custom-alert");

        // نغير النصوص للأزرار
        Button okBtn = (Button) pane.lookupButton(ButtonType.OK);
        okBtn.setText("حسناً");
        okBtn.getStyleClass().add("button-primary");

        alert.showAndWait();
    }
}
