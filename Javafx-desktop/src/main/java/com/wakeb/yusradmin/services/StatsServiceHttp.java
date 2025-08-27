package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wakeb.yusradmin.models.ReviewResponseDTO;

import java.util.*;

public class StatsServiceHttp {
    private final ApiClient api;
    public StatsServiceHttp(ApiClient api) { this.api = api; }

    public long getTotalUsers() throws Exception {
        return api.get("/api/admin/count/users", new TypeReference<Long>() {});
    }

    public long getTotalReviews() throws Exception {
        return api.get("/api/admin/count/reviews", new TypeReference<Long>() {});
    }

    public long getTotalPlaces() throws Exception {
        return api.get("/api/admin/count/places", new TypeReference<Long>() {});
    }

    // Remove the average rating method since it's not implemented in backend
    // public double getAverageRating() throws Exception {
    //     return api.get("/api/admin/averageRating", new TypeReference<Double>() {});
    // }

    public List<ReviewResponseDTO> getRecentReviews() throws Exception {
        return api.get("/api/admin/last24hours", new TypeReference<List<ReviewResponseDTO>>() {});
    }

    public Map<String, Integer> placesByCategory() throws Exception {
        List<Object[]> rows = api.get(
                "/api/admin/count/placeCategory",
                new TypeReference<List<Object[]>>() {}
        );

        Map<String, Integer> out = new LinkedHashMap<>();
        if (rows != null) {
            for (Object[] row : rows) {
                if (row == null || row.length < 2) continue;

                String category = String.valueOf(row[0]);
                Object n = row[1];
                int count = (n instanceof Number)
                        ? ((Number) n).intValue()
                        : Integer.parseInt(String.valueOf(n));

                out.put(category, count);
            }
        }
        return out;
    }

    public Map<String, Integer> reviewsByCategory() throws Exception {
        List<Object[]> rows = api.get(
                "/api/admin/reviewsbycategory",
                new TypeReference<List<Object[]>>() {}
        );

        Map<String, Integer> out = new LinkedHashMap<>();
        if (rows != null) {
            for (Object[] row : rows) {
                if (row == null || row.length < 2) continue;

                String category = String.valueOf(row[0]);
                Object n = row[1];
                int count = (n instanceof Number)
                        ? ((Number) n).intValue()
                        : Integer.parseInt(String.valueOf(n));

                out.put(category, count);
            }
        }
        return out;
    }
}