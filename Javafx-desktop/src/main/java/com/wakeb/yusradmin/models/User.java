package com.wakeb.yusradmin.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    public User(String UserName , String userEmail) {
        this.userName =  UserName;
        this.userEmail = userEmail;
    }

    public User() {};

    @JsonProperty("userId")
    private long id;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("userEmail")
    private String userEmail;

    @JsonProperty("hasRole")
    private String role;

    @JsonProperty("isBlocked")
    private boolean blocked;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}
