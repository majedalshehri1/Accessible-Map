package com.wakeb.yusradmin.dto;

public class DashboardStats {
    private long totalUsers;
    private long totalPlaces;
    private long totalReviews;
    private double averageRating;

    public DashboardStats(long totalUsers, long totalPlaces, long totalReviews, double averageRating) {
        this.totalUsers = totalUsers;
        this.totalPlaces = totalPlaces;
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalPlaces() {
        return totalPlaces;
    }

    public void setTotalPlaces(long totalPlaces) {
        this.totalPlaces = totalPlaces;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}