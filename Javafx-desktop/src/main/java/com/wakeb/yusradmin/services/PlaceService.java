package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wakeb.yusradmin.dto.PlaceUpdateDto;
import com.wakeb.yusradmin.models.PageResponse;
import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.utils.CATEGORY;
import javafx.concurrent.Task;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PlaceService {
    private final ApiClient api;

    public PlaceService() {
        this.api = new ApiClient(AuthService.getInstance().getBaseUrl());
    }

    // === Get all places (ADMIN) ===
    public Task<PageResponse<Place>> getAllPlaces(int page, int size) {
        return new Task<>() {
            @Override
            protected PageResponse<Place> call() throws Exception {
                try {
                    return api.get(
                            "/admin/all/places?page=" + page + "&size=" + size,
                            new TypeReference<PageResponse<Place>>() {}
                    );
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
                }
            }
        };
    }

    // === Search places by name ===
    public Task<List<Place>> getPlacesByName(String name) {
        return new Task<>() {
            @Override
            protected List<Place> call() throws Exception {
                if (name == null || name.isBlank()) {
                    throw new IllegalArgumentException("Place name cannot be empty");
                }

                try {
                    String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                    return api.get(
                            "/place/search?search=" + encodedName,
                            new TypeReference<List<Place>>() {}
                    );
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
                }
            }
        };
    }

    // === Get places by category paginated ===
    public Task<PageResponse<Place>> getPlacesByCategory(CATEGORY category, int page, int size) {
        return new Task<>() {
            @Override
            protected PageResponse<Place> call() throws Exception {
                if (category == null) {
                    throw new IllegalArgumentException("Place category cannot be null");
                }

                try {
                    if (category == CATEGORY.ALL) {
                        return api.get(
                                "/admin/all/places?page=" + page + "&size=" + size,
                                new TypeReference<PageResponse<Place>>() {}
                        );
                    }

                    return api.get(
                            "/place/category?category=" + category.getValue() + "&page=" + page + "&size=" + size,
                            new TypeReference<PageResponse<Place>>() {}
                    );

                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
                }
            }
        };
    }


    // === Search places with pagination ===
    public Task<List<Place>> searchPlaces(String query) {
        return new Task<>() {
            @Override
            protected List<Place> call() throws Exception {
                try {
                    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                    return api.get(
                            "/place/search?search=" + encodedQuery,
                            new TypeReference<List<Place>>() {}
                    );
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
                }
            }
        };
    }


    // === Delete a place by ID ===
    public Task<Void> deletePlaceById(long id) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (id < 0) throw new IllegalArgumentException("Place id cannot be negative");

                try {
                    api.delete("/admin/delete/place/" + id);
                    return null;
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
                }
            }
        };
    }

    // === Update a place by ID ===
    public Task<Place> updatePlaceById(long placeId, PlaceUpdateDto placeUpdateDto) {
        return new Task<>() {
            @Override
            protected Place call() throws Exception {
                if (placeId < 0) throw new IllegalArgumentException("Place id cannot be negative");

                try {
                    return api.put(
                            "/admin/update/place/" + placeId,
                            placeUpdateDto,
                            new TypeReference<Place>() {}
                    );
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
                }
            }
        };
    }

    // === Error handling  ===
    private RuntimeException handleAPIErrors(int statusCode) {
        switch (statusCode) {
            case 400:
                return new IllegalArgumentException("Bad Request");
            case 401:
                return new SecurityException("Authentication required");
            case 404:
                return new IllegalArgumentException("Endpoint not found");
            case 500:
                return new RuntimeException("Server error");
            default:
                return new RuntimeException("Unexpected error: " + statusCode);
        }
    }
}