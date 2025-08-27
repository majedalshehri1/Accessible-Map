package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.PlaceUpdateDto;
import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.models.User;
import com.wakeb.yusradmin.services.PlaceService;
import com.wakeb.yusradmin.util.FXUtil;
import com.wakeb.yusradmin.util.place.AccessibilityFeaturesCell;
import com.wakeb.yusradmin.util.place.ActionsBtnCell;
import com.wakeb.yusradmin.util.place.ImageCell;
import com.wakeb.yusradmin.util.place.LinkPlaceCell;
import com.wakeb.yusradmin.utils.AccessibilityFeatures;
import com.wakeb.yusradmin.utils.CATEGORY;
import com.wakeb.yusradmin.utils.HostServicesSinglton;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;

public class PlacesController {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<CATEGORY> filterComboBox;

    @FXML
    private TableView<Place> placesTable;
    @FXML
    private TableColumn<Place, Void> photoColumn;
    @FXML
    private TableColumn<Place, String> nameColumn;
    @FXML
    private TableColumn<Place, String> typeColumn;
    @FXML
    private TableColumn<Place, Void> servicesColumn;
    @FXML
    private TableColumn<Place, Void> locationColumn;
    @FXML
    private TableColumn<Place, Void> actionsColumn;

    private PlaceService placeService;
    private ObservableList<Place> places;

    private static final double[] COLUMN_WIDTH_PERCENTAGES = {0.12, 0.15, 0.12, 0.22, 0.12, 0.27};

    @FXML
    private void initialize() {
        System.out.println("PlacesController initialized");
        placeService = new PlaceService();
        places = FXCollections.observableArrayList();

        filterComboBox.setItems(FXCollections.observableArrayList(CATEGORY.values()));
        filterComboBox.setCellFactory(param -> new ListCell<CATEGORY>() {
            @Override
            protected void updateItem(CATEGORY item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });

        // Set the buttonCell to display the selected item's label
        filterComboBox.setButtonCell(new ListCell<CATEGORY>() {
            @Override
            protected void updateItem(CATEGORY item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });

        ReadOnlyDoubleProperty tableWidthProperty = placesTable.widthProperty();

        photoColumn.prefWidthProperty().bind(tableWidthProperty.multiply(COLUMN_WIDTH_PERCENTAGES[0]));
        nameColumn.prefWidthProperty().bind(tableWidthProperty.multiply(COLUMN_WIDTH_PERCENTAGES[1]));
        typeColumn.prefWidthProperty().bind(tableWidthProperty.multiply(COLUMN_WIDTH_PERCENTAGES[2]));
        servicesColumn.prefWidthProperty().bind(tableWidthProperty.multiply(COLUMN_WIDTH_PERCENTAGES[3]));
        locationColumn.prefWidthProperty().bind(tableWidthProperty.multiply(COLUMN_WIDTH_PERCENTAGES[4]));
        actionsColumn.prefWidthProperty().bind(tableWidthProperty.multiply(COLUMN_WIDTH_PERCENTAGES[5]));

        placesTable.setItems(places);

        photoColumn.setCellFactory(c -> new ImageCell());
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPlaceName()));
        typeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().getLabel()));
        servicesColumn.setCellFactory(c -> new AccessibilityFeaturesCell());
        locationColumn.setCellFactory(c -> new LinkPlaceCell(this::onLinkClick));
        actionsColumn.setCellFactory(c -> new ActionsBtnCell(this::onEdit, this::onDelete));

        searchButton.setOnAction(e -> {
            loadPlaces();
        });

        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadPlaces();
        });

        loadPlaces();
    }

    private void loadPlaces() {
        // TODO: Set loading state to true
        System.out.println("PlacesController loadPlaces");
//        Task<List<Place>> fetchTask = placeService.getPlacesByName("دله");
        Task<List<Place>> fetchTask;

        if (searchField.getText() != null && !searchField.getText().isEmpty()) {
            fetchTask = placeService.getPlacesByName(searchField.getText());
        } else if (filterComboBox.getValue() != null
                && filterComboBox.getSelectionModel().getSelectedItem() != null
                && filterComboBox.getSelectionModel().getSelectedItem() != CATEGORY.ALL
        ) {
            System.out.println(filterComboBox.getSelectionModel().getSelectedItem());
            fetchTask = placeService.getPlacesByCategory(filterComboBox.getSelectionModel().getSelectedItem());
        } else {
            filterComboBox.getSelectionModel().getSelectedItem();
            fetchTask = placeService.getAllPlaces();
        }

        // TODO: bind progress and message properties

        fetchTask.setOnSucceeded(event -> {
            this.places.setAll(fetchTask.getValue());
            System.out.println("PlacesController loadPlaces: " + this.places.size());
            for (Place place : this.places) {
                System.out.println(place);
            }
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

    private void onDelete(Place place) {
        if (!FXUtil.confirm("Confirm Delete", "Delete place \"" + place.getPlaceName() + "\"?")) return;
        Task<Void> deletePlaceTask = placeService.deletePlaceById(place.getId());
        deletePlaceTask.setOnSucceeded(e -> loadPlaces());
        deletePlaceTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                // TODO: Set loading state to false
                Throwable exception = deletePlaceTask.getException();
                handleErrors(exception);
            });
        });
        Thread deletePlaceThread = new Thread(deletePlaceTask);
        deletePlaceThread.setDaemon(true);
        System.out.println("delete thread started");
        deletePlaceThread.start();

        // TODO: bind progress and message properties
    }

    private void onEdit(Place place) {
        TextInputDialog dlg = new TextInputDialog(place.getPlaceName());
        dlg.setTitle("Edit Place");
        dlg.setHeaderText(null);
        dlg.setContentText("New Place Name:");
        dlg.showAndWait().ifPresent(name -> {
            place.setPlaceName(name);
            Task<Place> editPlaceTask = placeService.updatePlaceById(new PlaceUpdateDto(place.getId(), name, place.getCategory().getValue()));
            editPlaceTask.setOnSucceeded(e -> loadPlaces());
            editPlaceTask.setOnFailed(e -> {
                Platform.runLater(() -> {
                    // TODO: Set loading state to false
                    Throwable exception = editPlaceTask.getException();
                    handleErrors(exception);
                });
            });

            Thread editPlaceThread = new Thread(editPlaceTask);
            editPlaceThread.setDaemon(true);
            System.out.println("edit thread started");
            editPlaceThread.start();
        });
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

    private String buildGoogleMapsUrl(String latitude, String longitude) {
        return "https://www.google.com/maps/@" + latitude + "," + longitude + ",12z";
    }

    private void onLinkClick(Place place) {
        String mapUrl = buildGoogleMapsUrl(place.getLatitude(), place.getLongitude());
        HostServicesSinglton.getHostServices().showDocument(mapUrl);
    }
}
