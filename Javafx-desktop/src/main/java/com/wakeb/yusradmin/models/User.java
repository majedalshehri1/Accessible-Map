package com.wakeb.yusradmin.models;

public class User {
    private String username;
    private String email;

    public User() {} // للـGson

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters/Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
