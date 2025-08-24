package com.main.app.dto;

public class AuthResponse {
    private String accessToken;
    private Long userId;
    private String username;
    private String email;
    private String message;

    public AuthResponse(String message) {
        this.message = message;
    }
    public AuthResponse(String accessToken, Long userId, String username, String email) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getAccessToken() { return accessToken; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}