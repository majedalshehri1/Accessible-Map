package com.wakeb.yusradmin.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wakeb.yusradmin.dto.LoginRequest;
import com.wakeb.yusradmin.models.User;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.LocalDateTime;
import java.util.prefs.Preferences;

/**
 * AuthService
 *
 * - Singleton service that handles authentication & session state.
 * - Talks to backend (login, validate) and persists the session locally.
 * - Exposes async JavaFX Tasks so UI stays responsive.
 */
public class AuthService {


    // Singleton pattern (only one instance)
    private static final AuthService INSTANCE = new AuthService();
    public static AuthService getInstance() { return INSTANCE; }


    // HTTP & JSON helpers
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // Backend endpoints (adjust if needed)
    private final String baseUrl = "http://localhost:8081/api";
    private final String validatePath = "/auth/me"; // or "/auth/validate"

    // Local storage for session persistence
    private final Preferences preferences = Preferences.userNodeForPackage(AuthService.class);


    // In-memory session state
    private String currentToken;
    private User currentUser;
    private LocalDateTime tokenExpiry;

    /**
     * Private ctor (singleton):
     * - Load any stored session.
     * - Immediately validate it against the backend.
     */
    private AuthService() {
        loadStoredAuth();
        validateSessionSync(); // if server down/invalid → clears session
    }


    // Public API for UI layer
    /**
     * Login:
     * - POST /auth/login with email/password.
     * - On 2xx: extract token, set temporary session, validate it with backend, then persist.
     * - Returns LoginResult via JavaFX Task.
     */
    public Task<LoginResult> loginAsync(LoginRequest loginRequest) {
        return new Task<>() {
            @Override
            protected LoginResult call() throws Exception {
                updateMessage("Authenticating...");

                // Build request body
                JsonObject body = new JsonObject();
                body.addProperty("email", loginRequest.getEmail());
                body.addProperty("password", loginRequest.getPassword());

                // HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/auth/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();

                HttpResponse<String> response = safeSend(request);
                if (response == null) {
                    return new LoginResult(false, null, "الخادم غير متاح حاليا", null);
                }
                int sc = response.statusCode();
                if (sc == 401) {
                    return new LoginResult(false, null, "البريد الإلكتروني أو كلمة المرور غير صحيحة.", null);
                }
                if (sc == 403) {
                    return new LoginResult(false, null, "تم حظر الحساب.", null);
                }
                if (sc / 100 != 2) {
                    return new LoginResult(false, null, "حدث خطأ غير متوقع. حاول لاحقًا.", null);
                }
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);

                String token =
                        (json.has("accessToken") && !json.get("accessToken").isJsonNull())
                                ? json.get("accessToken").getAsString()
                                : (json.has("token") && !json.get("token").isJsonNull()
                                ? json.get("token").getAsString()
                                : null);

                if (token == null) {
                    return new LoginResult(false, null, "Token missing in response.", null);
                }
                User fetched = fetchUserFromMe(token);
                if (fetched == null) {
                    return new LoginResult(false, null, "Session validation failed.", null);
                }
                if (!hasAdminRole(fetched.getRole())) {
                    return new LoginResult(false, null, "حظر الوصول: فقط للمسؤولين", null);
                }

                currentToken = token;
                currentUser  = fetched;
                tokenExpiry  = LocalDateTime.now().plusHours(8);
                storeAuth(currentToken, currentUser, tokenExpiry);

                String message = "Login successful";
                return new LoginResult(true, currentUser, message, currentToken);
            }
        };
    }
    private User fetchUserFromMe(String token) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + validatePath))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> res = safeSend(req);
        if (res == null || res.statusCode() / 100 != 2) {
            return null;
        }

        try {
            var json = gson.fromJson(res.body(), com.google.gson.JsonObject.class);
            String username = json.has("username") && !json.get("username").isJsonNull()
                    ? json.get("username").getAsString() : null;
            String email = json.has("email") && !json.get("email").isJsonNull()
                    ? json.get("email").getAsString() : null;
            String role = null;
            if (json.has("role") && !json.get("role").isJsonNull())
                role = json.get("role").getAsString();
            else if (json.has("hasRole") && !json.get("hasRole").isJsonNull())
                role = json.get("hasRole").getAsString();

            User u = new User();
            u.setUserName(username);
            u.setUserEmail(email);
            u.setRole(role);
            return u;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Logout:
     * - Clears local session (and could call backend later if needed).
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

    /**
     * Validate current session with backend (async):
     * - Useful for guards or periodic checks by the UI layer.
     */
    public Task<Boolean> validateSessionAsync() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                return validateSessionSync();
            }
        };
    }

    /**
     * Local check only:
     * - Returns true if we still have a token and it's not expired.
     * - Does NOT ping the server (use validateSessionAsync for that).
     */
    public boolean isAuthenticated() {
        return currentToken != null
                && tokenExpiry != null
                && LocalDateTime.now().isBefore(tokenExpiry);
    }

    public String getCurrentToken() { return isAuthenticated() ? currentToken : null; }
    public User getCurrentUser()    { return isAuthenticated() ? currentUser : null; }

    // Validation & HTTP helpers
    /**
     * Server validation (sync):
     * - GET /auth/me with Bearer token.
     * - On 2xx: parse minimal user data and keep the session.
     * - On any failure: clear session.
     */
    private boolean validateSessionSync() {
        if (currentToken == null) return false;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + validatePath))
                .header("Authorization", "Bearer " + currentToken)
                .GET()
                .build();

        HttpResponse<String> response = safeSend(request);
        if (response == null) { // server unreachable
            clearAuth();
            return false;
        }

        if (response.statusCode() / 100 == 2) {
            try {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                String username = json.has("username") && !json.get("username").isJsonNull()
                        ? json.get("username").getAsString() : null;
                String email = json.has("email") && !json.get("email").isJsonNull()
                        ? json.get("email").getAsString() : null;
                String role = null;
                if (json.has("role") && !json.get("role").isJsonNull())
                    role = json.get("role").getAsString();
                else if (json.has("hasRole") && !json.get("hasRole").isJsonNull())
                    role = json.get("hasRole").getAsString();

                if (role != null) role = role.trim();

                User u = new User();
                u.setUserName(username);
                u.setUserEmail(email);
                u.setRole(role);
                currentUser = u;
                return true;
            } catch (Exception ignore) {
                clearAuth();
                return false;
            }
        }

        // 401/403/5xx → invalidate
        clearAuth();
        return false;
    }

    /**
     * Send HTTP request safely:
     * - Returns null when server is down / timed out / I/O errors happen.
     * - Keeps callers simple and consistent.
     */
    private HttpResponse<String> safeSend(HttpRequest req) {
        try {
            return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException | ConnectException e) {
            return null; // server unreachable
        } catch (IOException | InterruptedException e) {
            return null; // generic I/O problems
        }
    }
    // ===== helpers for role checks =====
    public static boolean hasAdminRole(String role) {
        if (role == null) return false;
        String r = role.trim();
        return r.equalsIgnoreCase("ADMIN") || r.equalsIgnoreCase("ROLE_ADMIN");
    }
    public static boolean isAdminUser(com.wakeb.yusradmin.models.User u) {
        return u != null && hasAdminRole(u.getRole());
    }

    // Persistence (Preferences) helpers
    /**
     * Persist current session (token + expiry + user JSON) to Preferences.
     */
    private void storeAuth(String token, User user, LocalDateTime expiry) {
        preferences.put("auth_token", token);
        preferences.put("token_expiry", expiry.toString());
        preferences.put("current_user", user != null ? gson.toJson(user) : "");
    }

    /**
     * Load session from Preferences:
     * - Restores token/user if not expired, otherwise clears everything.
     */
    private void loadStoredAuth() {
        String storedToken = preferences.get("auth_token", null);
        String storedExpiry = preferences.get("token_expiry", null);
        String storedUser = preferences.get("current_user", "");

        if (storedToken == null || storedExpiry == null) {
            clearAuth();
            return;
        }

        try {
            LocalDateTime exp = LocalDateTime.parse(storedExpiry);
            if (LocalDateTime.now().isAfter(exp)) {
                clearAuth();
                return;
            }
            currentToken = storedToken;
            tokenExpiry = exp;
            if (!storedUser.isBlank()) currentUser = gson.fromJson(storedUser, User.class);
        } catch (Exception e) {
            clearAuth();
        }
    }

    /**
     * Clear in-memory + persisted session fully.
     */
    private void clearAuth() {
        currentToken = null;
        currentUser = null;
        tokenExpiry = null;
        preferences.remove("auth_token");
        preferences.remove("token_expiry");
        preferences.remove("current_user");
    }

    // DTO for login results

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
        public User getUser()      { return user; }
        public String getMessage() { return message; }
        public String getToken()   { return token; }
    }
    // In AuthService.java, add these methods:
    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBearerToken() {
        return isAuthenticated() ? "Bearer " + currentToken : null;
    }
}