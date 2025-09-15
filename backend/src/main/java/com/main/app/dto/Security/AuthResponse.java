package com.main.app.dto.Security;

import com.main.app.Enum.Role;

public class AuthResponse {
    private String accessToken;
    private Long userId;
    private String username;
    private String email;
    private Role role;

    public AuthResponse(String accessToken, Long userId, String username, String email ,  Role role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;

    }

    public String getAccessToken() { return accessToken; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}