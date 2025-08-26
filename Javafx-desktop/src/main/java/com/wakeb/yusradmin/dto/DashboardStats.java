package com.wakeb.yusradmin.dto;
// Dummy data purpose for overview

public class DashboardStats {
    private long totalUsers;
    private long totalReviews;
    private long totalPlaces;
    private double averageRating;

    public DashboardStats() {
    }

    public DashboardStats(long totalUsers, long totalReviews, long totalPlaces, double averageRating) {
        this.totalUsers = totalUsers;
        this.totalReviews = totalReviews;
        this.totalPlaces = totalPlaces;
        this.averageRating = averageRating;
    }

    // Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public long getTotalPlaces() {
        return totalPlaces;
    }

    public void setTotalPlaces(long totalPlaces) {
        this.totalPlaces = totalPlaces;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}