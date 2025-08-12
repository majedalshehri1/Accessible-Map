package com.main.app.dto;

public class AuthResponse {
    private String accessToken;
    private String username;
    private String email;

    public AuthResponse(String accessToken, String username, String email) {
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
    }

    public String getAccessToken() { return accessToken; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}