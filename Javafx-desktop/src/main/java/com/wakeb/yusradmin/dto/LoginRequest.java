package com.wakeb.yusradmin.dto;

/**
 * LoginRequest
 *
 * DTO (Data Transfer Object) used to send login credentials to the backend.
 *
 * - username: The email/username entered by the user
 * - password: The password entered by the user
 *
 * Note:
 * This class is just a container (no logic).
 */
public class LoginRequest {
    private String username;
    private String password;

    // Constructor
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
