package com.wakeb.yusradmin.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wakeb.yusradmin.dto.PlaceMapDTO;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;

public class PlaceService {
    private final ApiClient apiClient;
    private final Gson gson;

    public PlaceService() {
        AuthService authService = AuthService.getInstance();
        this.apiClient = new ApiClient(authService.getBaseUrl());
        this.gson = new Gson();
    }

    public Task<List<PlaceMapDTO>> getAllPlacesAsync() {
        Task<List<PlaceMapDTO>> task = new Task<>() {
            @Override
            protected List<PlaceMapDTO> call() throws Exception {
                // ✅ CORRECTED: Use the full endpoint path
                HttpResponse<String> response = apiClient.getRaw("/admin/all/places");

                if (response.statusCode() == 200) {
                    Type listType = new TypeToken<List<PlaceMapDTO>>(){}.getType();
                    return gson.fromJson(response.body(), listType);
                } else {
                    String errorBody = response.body();
                    throw new RuntimeException("Failed to fetch places: " + response.statusCode() +
                            " - " + (errorBody != null ? errorBody : "No response body"));
                }
            }
        };
        return task;
    }
}