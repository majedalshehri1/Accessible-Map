package com.wakeb.yusradmin.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wakeb.yusradmin.dto.LoginRequest;
import com.wakeb.yusradmin.models.User;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.prefs.Preferences;

/**
 * AuthService
 *
 * Responsibilities:
 * - Handles authentication logic (login, logout).
 * - Stores/loads tokens and user session from Preferences.
 * - Exposes async Tasks for network operations (non-blocking).
 *
 * Currently:
 * - Methods are scaffolded (loginAsync, logoutAsync, isAuthenticated).
 * - TODO: Connect with actual backend API.
 */
public class AuthService {
    private final HttpClient httpClient;
    private final Gson gson;
    private final String baseUrl;
    private final Preferences preferences;

    private String currentToken;
    private User currentUser;
    private LocalDateTime tokenExpiry;

    // Constructor
    public AuthService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.baseUrl = "https://api.yourapp.com/v1"; // TODO(team): move to config
        this.preferences = Preferences.userNodeForPackage(AuthService.class);
        loadStoredAuth(); // Restore any stored session
    }

    /**
     * Async login Task
     * - Builds request
     * - Sends to backend
     * - Parses response
     *
     * TODO(team): Adjust based on actual API contract.
     */
    public Task<LoginResult> loginAsync(LoginRequest loginRequest) {
        return new Task<>() {
            @Override
            protected LoginResult call() throws Exception {
                updateMessage("Authenticating...");
                updateProgress(0, 100);

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("username", loginRequest.getUsername());
                requestBody.addProperty("password", loginRequest.getPassword());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/auth/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // TODO(team): Parse response and build LoginResult
                return new LoginResult(false, null, "Stub login result", null);
            }
        };
    }

    /**
     * Async logout Task
     * TODO(team): Call backend logout API if available.
     */
    public Task<Void> logoutAsync() {
        return new Task<>() {
            @Override
            protected Void call() {
                clearAuth();
                return null;
            }
        };
    }

    // --- Session Helpers ---
    public boolean isAuthenticated() {
        return currentToken != null && tokenExpiry != null && LocalDateTime.now().isBefore(tokenExpiry);
    }

    public String getCurrentToken() { return isAuthenticated() ? currentToken : null; }
    public User getCurrentUser() { return isAuthenticated() ? currentUser : null; }

    // --- Internal storage ---
    private void storeAuth(String token, User user, LocalDateTime expiry) {
        preferences.put("auth_token", token);
        preferences.put("token_expiry", expiry.toString());
        if (user != null) {
            preferences.put("current_user", gson.toJson(user));
        }
    }

    private void loadStoredAuth() {
        String storedToken = preferences.get("auth_token", null);
        String storedExpiry = preferences.get("token_expiry", null);
        String storedUser = preferences.get("current_user", null);

        // TODO(team): Validate token expiry and restore session if valid
    }

    private void clearAuth() {
        currentToken = null;
        currentUser = null;
        tokenExpiry = null;
        preferences.remove("auth_token");
        preferences.remove("token_expiry");
        preferences.remove("current_user");
    }

    /**
     * Result wrapper for login
     */
    public static class LoginResult {
        private final boolean success;
        private final User user;
        private final String message;
        private final String token;

        public LoginResult(boolean success, User user, String message, String token) {
            this.success = success;
            this.user = user;
            this.message = message;
            this.token = token;
        }

        public boolean isSuccess() { return success; }
        public User getUser() { return user; }
        public String getMessage() { return message; }
        public String getToken() { return token; }
    }
}
