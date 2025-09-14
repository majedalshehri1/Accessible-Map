package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wakeb.yusradmin.models.PaginatedResponse;
import com.wakeb.yusradmin.models.ReviewRequestDTO;
import com.wakeb.yusradmin.models.ReviewResponseDTO;
import javafx.concurrent.Task;

import java.util.List;


public class ReviewService {
    private final ApiClient api;

    public ReviewService() {
        this.api = new ApiClient(AuthService.getInstance().getBaseUrl());
    }
    /** GET /api/admin/all/reviews?page={page}&size={size} */
    public Task<PaginatedResponse<ReviewResponseDTO>> getAllReviewsAsync(int page, int size) {
        return new Task<>() {
            @Override
            protected PaginatedResponse<ReviewResponseDTO> call() throws Exception {
                return api.get(
                        "/admin/all/reviews?page=" + page + "&size=" + size,
                        new TypeReference<PaginatedResponse<ReviewResponseDTO>>() {}
                );
            }
        };
    }

    /** GET /api/admin/all/reviews (all reviews without pagination) */
    public Task<List<ReviewResponseDTO>> getAllReviewsAsync() {
        return new Task<>() {
            @Override
            protected List<ReviewResponseDTO> call() throws Exception {
                return api.get(
                        "/admin/all/reviews",
                        new TypeReference<List<ReviewResponseDTO>>() {}
                );
            }
        };
    }

    /** DELETE /api/admin/delete/review/{id} */
    public Task<Void> deleteReviewAsync(long reviewId) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                api.delete("/admin/delete/review/" + reviewId);
                return null;
            }
        };
    }

    /** PUT /api/admin/update/review/{id}  (body: ReviewRequestDTO) */
    public Task<ReviewResponseDTO> updateReviewAsync(long reviewId, ReviewRequestDTO dto) {
        return new Task<>() {
            @Override
            protected ReviewResponseDTO call() throws Exception {
                return api.put(
                        "/admin/update/review/" + reviewId,
                        dto,
                        new TypeReference<ReviewResponseDTO>() {}
                );
            }
        };
    }
}