package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeb.yusradmin.dto.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/admin";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static ApiClient instance;

    private ApiClient() {}

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    private <T> CompletableFuture<T> get(String endpoint, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), responseType);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse response", e);
                        }
                    } else {
                        throw new RuntimeException("HTTP error: " + response.statusCode());
                    }
                });
    }

    private <T> CompletableFuture<List<T>> getList(String endpoint, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(),
                                    objectMapper.getTypeFactory().constructCollectionType(List.class, responseType));
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse response", e);
                        }
                    } else {
                        throw new RuntimeException("HTTP error: " + response.statusCode());
                    }
                });
    }

    // Statistics endpoints
    public CompletableFuture<Long> getUserCount() {
        return get("/count/users", Long.class);
    }

    public CompletableFuture<Long> getPlaceCount() {
        return get("/count/places", Long.class);
    }

    public CompletableFuture<Long> getReviewCount() {
        return get("/count/reviews", Long.class);
    }

    public CompletableFuture<Double> getAverageRating() {
        // You might need to create this endpoint in your backend
        return CompletableFuture.completedFuture(4.5); // Placeholder
    }

    // Data endpoints
    public CompletableFuture<List<ReviewResponseDTO>> getLatestReviews() {
        return getList("/last24hours", ReviewResponseDTO.class);
    }

    public CompletableFuture<List<Object[]>> getPlacesByCategory() {
        return getList("/count/placeCategory", Object[].class);
    }

    public CompletableFuture<List<Object[]>> getReviewsByCategory() {
        return getList("/reviewsbycategory", Object[].class);
    }

    public CompletableFuture<List<UserDto>> getAllUsers() {
        return getList("/all/users", UserDto.class);
    }

    public CompletableFuture<List<PlaceDto>> getAllPlaces() {
        return getList("/all/places", PlaceDto.class);
    }

    // Action endpoints
    public CompletableFuture<Boolean> blockUser(Long userId) {
        return put("/users/" + userId + "/block", Boolean.class);
    }

    public CompletableFuture<Boolean> unblockUser(Long userId) {
        return put("/users/" + userId + "/unblock", Boolean.class);
    }

    public CompletableFuture<Boolean> deleteReview(Long reviewId) {
        return delete("/delete/review/" + reviewId, Boolean.class);
    }

    public CompletableFuture<Boolean> deletePlace(Long placeId) {
        return delete("/delete/place/" + placeId, Boolean.class);
    }

    private <T> CompletableFuture<T> put(String endpoint, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), responseType);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse response", e);
                        }
                    } else {
                        throw new RuntimeException("HTTP error: " + response.statusCode());
                    }
                });
    }

    private <T> CompletableFuture<T> delete(String endpoint, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), responseType);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse response", e);
                        }
                    } else {
                        throw new RuntimeException("HTTP error: " + response.statusCode());
                    }
                });
    }
}