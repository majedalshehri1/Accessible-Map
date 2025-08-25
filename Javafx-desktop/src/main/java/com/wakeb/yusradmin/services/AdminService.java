package com.wakeb.yusradmin.services;

import com.wakeb.yusradmin.dto.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AdminService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public CompletableFuture<DashboardStats> loadDashboardStats() {
        CompletableFuture<Long> userCount = apiClient.getUserCount();
        CompletableFuture<Long> placeCount = apiClient.getPlaceCount();
        CompletableFuture<Long> reviewCount = apiClient.getReviewCount();
        CompletableFuture<Double> avgRating = apiClient.getAverageRating();

        return CompletableFuture.allOf(userCount, placeCount, reviewCount, avgRating)
                .thenApply(ignored -> new DashboardStats(
                        userCount.join(),
                        placeCount.join(),
                        reviewCount.join(),
                        avgRating.join()
                ));
    }

    public CompletableFuture<List<ReviewResponseDTO>> loadLatestReviews() {
        return apiClient.getLatestReviews();
    }

    public CompletableFuture<List<CategoryCount>> loadPlacesByCategory() {
        return apiClient.getPlacesByCategory()
                .thenApply(arrayList -> arrayList.stream()
                        .map(array -> new CategoryCount(
                                array[0].toString(),
                                Integer.parseInt(array[1].toString())
                        ))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<List<CategoryCount>> loadReviewsByCategory() {
        return apiClient.getReviewsByCategory()
                .thenApply(arrayList -> arrayList.stream()
                        .map(array -> new CategoryCount(
                                array[0].toString(),
                                Integer.parseInt(array[1].toString())
                        ))
                        .collect(Collectors.toList()));
    }

    public CompletableFuture<List<UserDto>> loadAllUsers() {
        return apiClient.getAllUsers();
    }

    public CompletableFuture<List<PlaceDto>> loadAllPlaces() {
        return apiClient.getAllPlaces();
    }

    public CompletableFuture<Boolean> blockUser(Long userId) {
        return apiClient.blockUser(userId);
    }

    public CompletableFuture<Boolean> unblockUser(Long userId) {
        return apiClient.unblockUser(userId);
    }

    public CompletableFuture<Boolean> deleteReview(Long reviewId) {
        return apiClient.deleteReview(reviewId);
    }

    public CompletableFuture<Boolean> deletePlace(Long placeId) {
        return apiClient.deletePlace(placeId);
    }
}