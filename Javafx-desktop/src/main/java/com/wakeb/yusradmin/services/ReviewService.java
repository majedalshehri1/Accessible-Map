package com.wakeb.yusradmin.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wakeb.yusradmin.models.PlaceDto;
import com.wakeb.yusradmin.models.ReviewRequestDTO;
import com.wakeb.yusradmin.models.ReviewResponseDTO;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * ReviewService (client-side, JavaFX)
 *
 * Calls backend admin APIs:
 *  - GET  /api/admin/all/reviews
 *  - DELETE /api/admin/delete/review/{id}
 *  - PUT /api/admin/update/place/{id}
 *
 * Uses AuthService for base URL and Authorization header.
 * Returns JavaFX Tasks so calls are non-blocking.
 */
public class ReviewService {
    private final AuthService authService = AuthService.getInstance();
    private final HttpClient http;
    private final Gson gson = new Gson();
    /** e.g., http://localhost:8081/api/admin */
    private final String adminBase;

    public ReviewService() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        String base = authService.getBaseUrl();
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        this.adminBase = base + "/admin";  // ✅ Add this
    }

    // ---------------------------
    // Public async API methods
    // ---------------------------

    /** GET /api/admin/all/reviews */
    public Task<List<ReviewResponseDTO>> getAllReviewsAsync() {
        return new Task<>() {
            @Override protected List<ReviewResponseDTO> call() throws Exception {
                HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(adminBase + "/all/reviews"))
                        .timeout(Duration.ofSeconds(20))
                        .GET();
                addAuth(b);
                HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                ensure2xx(res, "getAllReviews");
                Type listType = new TypeToken<List<ReviewResponseDTO>>(){}.getType();
                return gson.fromJson(res.body(), listType);
            }
        };
    }

    /** DELETE /api/admin/delete/review/{id} */
    public Task<Void> deleteReviewAsync(long reviewId) {
        return new Task<>() {
            @Override protected Void call() throws Exception {
                HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(adminBase + "/delete/review/" + reviewId))
                        .timeout(Duration.ofSeconds(20))
                        .DELETE();
                addAuth(b);
                HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                ensure2xx(res, "deleteReview");
                return null;
            }
        };
    }

    /** PUT /api/admin/update/review/{id}  (body: ReviewRequestDTO) */
    public Task<ReviewResponseDTO> updateReviewAsync(long reviewId, ReviewRequestDTO dto) {
        return new Task<>() {
            @Override protected ReviewResponseDTO call() throws Exception {
                String body = gson.toJson(dto);
                HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(adminBase + "/update/review/" + reviewId))
                        .timeout(Duration.ofSeconds(20))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
                addAuth(b);
                HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                ensure2xx(res, "updateReview");
                return gson.fromJson(res.body(), ReviewResponseDTO.class);
            }
        };
    }

    // ---------------------------
    // Helpers
    // ---------------------------

    private void addAuth(HttpRequest.Builder b) {
        String bearer = authService.getBearerToken(); // e.g., "Bearer <jwt>"
        if (bearer != null && !bearer.isBlank()) {
            b.header("Authorization", bearer);
        }
    }

    private void ensure2xx(HttpResponse<?> res, String op) {
        int sc = res.statusCode();
        if (sc / 100 != 2) {
            throw new RuntimeException(op + " failed: " + sc + " -> " + res.body());
        }
    }



}