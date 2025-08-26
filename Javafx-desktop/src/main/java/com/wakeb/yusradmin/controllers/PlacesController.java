package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.services.PlaceService;
import com.wakeb.yusradmin.utils.CATEGORY;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;

public class PlacesController {
    private PlaceService placeService;
    private ObservableList<Place> places;

    @FXML
    private void initialize() {
        System.out.println("PlacesController initialized");
        // TODO: load places table
        placeService = new PlaceService();
        places = FXCollections.observableArrayList();
        loadPlaces();
    }

    private void loadPlaces() {
        // TODO: Set loading state to true
        System.out.println("PlacesController loadPlaces");
//        Task<List<Place>> fetchTask = placeService.getPlacesByName("دله");
        Task<List<Place>> fetchTask = placeService.getPlacesByCategory(CATEGORY.COFFEE);
        System.out.println("fetchTask: ");

        // TODO: bind progress and message properties

        fetchTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                List<Place> places = fetchTask.getValue();
                this.places.clear();
                this.places.addAll(places);
                // TODO: Set loading state to false
                for (Place place : places) {
                    System.out.println(place.toString());
                }
            });
        });

        fetchTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                // TODO: Set loading state to false
                Throwable exception = fetchTask.getException();
                handleErrors(exception);
            });
        });

        System.out.println("fetchTask started");

        Thread fetchThread = new Thread(fetchTask);
        fetchThread.setDaemon(true);
        System.out.println("Fetch thread started");
        fetchThread.start();
    }

    // method to handle errors and calls the showAlert() method
    private void handleErrors(Throwable error) {
        String errorMessage;
        System.out.println("error: " + error.getMessage());

        if (error instanceof SecurityException) {
            errorMessage = "Authentication required. Please login.";
        } else if (error instanceof IllegalArgumentException) {
            errorMessage = "Invalid request. Please try again.";
        } else if (error instanceof IOException) {
            errorMessage = "Network Error. Please try again.";
        } else {
            errorMessage = "unknown error. Please try again.";
        }

        showAlert("Error", errorMessage, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
