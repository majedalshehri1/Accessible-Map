package com.wakeb.yusradmin.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wakeb.yusradmin.dto.PlaceUpdateDto;
import com.wakeb.yusradmin.models.PageResponse;
import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.utils.AccessibilityFeatures;
import com.wakeb.yusradmin.utils.AccessibilityFeaturesTypeAdapter;
import com.wakeb.yusradmin.utils.CATEGORY;
import com.wakeb.yusradmin.utils.CategoryTypeAdapter;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PlaceService {
    private final HttpClient httpClient;
    private final Gson gson;
    private final String baseUrl;

    public PlaceService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(CATEGORY.class, new CategoryTypeAdapter())
                .registerTypeAdapter(AccessibilityFeatures[].class, new AccessibilityFeaturesTypeAdapter())
                .create();
        this.baseUrl = "http://localhost:8081/api";
    }

    public Task<PageResponse<Place>> getAllPlaces(int page, int size) {
        return new Task<>() {
            @Override
            protected PageResponse<Place> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/admin/all/places?page=" + page + "&size=" + size))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                return getPlacePageResponse(request);
            }
        };
    }


    public Task<List<Place>> getPlacesByName(String name) {
        return new Task<>() {
            @Override
            protected List<Place> call() throws Exception {
                try {
                    if (name == null || name.isEmpty()) {
                        throw new IllegalArgumentException("Place name cannot be empty");
                    }

                    updateProgress(0, 100);
                    updateMessage("Loading all places...");

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/place/search?search=" + name))
                            .header("Content-Type", "application/json")
                            .GET()
                            .build();

                    updateProgress(33, 100);

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    updateProgress(66, 100);

                    int statusCode = response.statusCode();

                    if (statusCode == 200) {
                        List<Place> places = gson.fromJson(response.body(), new TypeToken<List<Place>>() {
                        }.getType());
                        updateProgress(100, 100);
                        System.out.println(places);
                        return places != null ? places : new ArrayList<>();
                    } else {
                        throw handleAPIErrors(statusCode);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IOException | JsonSyntaxException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public Task<PageResponse<Place>> getPlacesByCategory(CATEGORY category, int page, int size) {
        return new Task<>() {
            @Override
            protected PageResponse<Place> call() throws Exception {
                try {
                    if (category == null) {
                        throw new IllegalArgumentException("Place category cannot be empty");
                    }

                    updateProgress(0, 100);
                    updateMessage("Loading all places...");

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/place/category?category=" + category.getValue() + "&page=" + page + "&size=" + size))
                            .header("Content-Type", "application/json")
                            .GET()
                            .build();

                    updateProgress(33, 100);

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    updateProgress(66, 100);

                    int statusCode = response.statusCode();

                    if (statusCode == 200) {
                        PageResponse<Place> places = gson.fromJson(response.body(), new TypeToken<PageResponse<Place>>() {
                        }.getType());
                        updateProgress(100, 100);
                        System.out.println(places);
                        return places != null ? places : new PageResponse<>();
                    } else {
                        throw handleAPIErrors(statusCode);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IOException | JsonSyntaxException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        };
    }
    public Task<PageResponse<Place>> searchPlaces(String query, int page, int size) {
        return new Task<>() {
            @Override
            protected PageResponse<Place> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/admin/all/places?page=" + page +
                                "&size=" + size + "&search=" + URLEncoder.encode(query, StandardCharsets.UTF_8)))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                return getPlacePageResponse(request);
            }
        };
    }

    private PageResponse<Place> getPlacePageResponse(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            PageResponse<Place> pageResp = gson.fromJson(response.body(),
                    new TypeToken<PageResponse<Place>>() {}.getType());
            return pageResp != null ? pageResp : new PageResponse<>();
        } else {
            throw handleAPIErrors(response.statusCode());
        }
    }

    public Task<Void> deletePlaceById(long id) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    if (id < 0) {
                        throw new IllegalArgumentException("Place id cannot be negative");
                    }

                    updateProgress(0, 100);
                    updateMessage("deleting place...");

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/admin/delete/place/" + id))
                            .header("Content-Type", "application/json")
                            .DELETE()
                            .build();

                    updateProgress(33, 100);

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    updateProgress(66, 100);

                    int statusCode = response.statusCode();

                    if (statusCode == 200) {
                        updateMessage("Place deleted successfully.");
                    } else {
                        throw handleAPIErrors(statusCode);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IOException | JsonSyntaxException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return null;
            }
        };
    }

    public Task<Place> updatePlaceById(PlaceUpdateDto placeUpdateDto) {
        return new Task<Place>() {
            @Override
            protected Place call() throws Exception {
                long placeId = placeUpdateDto.getId();

                try {
                    if (placeId < 0) {
                        throw new IllegalArgumentException("Place id cannot be negative");
                    }

                    String jsonRequest = gson.toJson(placeUpdateDto);

                    updateProgress(0, 100);
                    updateMessage("updating place...");

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/admin/update/place/" + placeId))
                            .header("Content-Type", "application/json")
                            .PUT(HttpRequest.BodyPublishers.ofString(jsonRequest))
                            .build();

                    updateProgress(33, 100);

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    updateProgress(66, 100);

                    int statusCode = response.statusCode();

                    if (statusCode == 200) {
                        updateMessage("Place updated successfully.");
                        return gson.fromJson(response.body(), Place.class);
                    } else {
                        System.out.println("statusCode: " + statusCode);
                        System.out.println("statusMessage: " + response.body());
                        throw handleAPIErrors(statusCode);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IOException | JsonSyntaxException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        };
    }

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