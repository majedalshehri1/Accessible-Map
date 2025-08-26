package com.wakeb.yusradmin.dto;

import java.time.LocalDateTime;
// Dummy data purpose for overview

public class ReviewResponseDTO {
    private String userName;
    private String placeName;
    private String description;
    private int rating;
    private LocalDateTime createdAt;

    public ReviewResponseDTO() {
    }

    public ReviewResponseDTO(String userName, String placeName, String description, int rating, LocalDateTime createdAt) {
        this.userName = userName;
        this.placeName = placeName;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}