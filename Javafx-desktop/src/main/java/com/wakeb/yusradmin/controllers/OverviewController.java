package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML private Label registeredUsersLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label totalPlacesLabel;
    @FXML private Label avgRatingLabel;

    @FXML private TableView<ReviewResponseDTO> reviewsTableView;
    @FXML private TableView<CategoryCount> placesByCategoryTable;
    @FXML private TableView<CategoryCount> reviewsByCategoryTable;

    @FXML private StackPane mapContainer;

    private WebView webView;
    private WebEngine webEngine;

    private final ObservableList<ReviewResponseDTO> reviewsData = FXCollections.observableArrayList();
    private final ObservableList<CategoryCount> placesCategoryData = FXCollections.observableArrayList();
    private final ObservableList<CategoryCount> reviewsCategoryData = FXCollections.observableArrayList();
    private final ObservableList<PlaceMapDTO> placesData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTables();
        setupMap();
        loadDashboardData();
        loadCategoryStats();
        loadPlacesForMap();
    }

    private void setupTables() {
        TableColumn<ReviewResponseDTO, String> userColumn = new TableColumn<>("المستخدم");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<ReviewResponseDTO, String> placeColumn = new TableColumn<>("المكان");
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("placeName"));

        TableColumn<ReviewResponseDTO, String> descriptionColumn = new TableColumn<>("التعليق");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ReviewResponseDTO, Integer> ratingColumn = new TableColumn<>("التقييم");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        TableColumn<ReviewResponseDTO, String> dateColumn = new TableColumn<>("التاريخ");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        reviewsTableView.getColumns().setAll(userColumn, placeColumn, descriptionColumn, ratingColumn, dateColumn);
        reviewsTableView.setItems(reviewsData);

        TableColumn<CategoryCount, String> categoryColumn = new TableColumn<>("الفئة");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<CategoryCount, Integer> countColumn = new TableColumn<>("العدد");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        placesByCategoryTable.getColumns().setAll(categoryColumn, countColumn);
        reviewsByCategoryTable.getColumns().setAll(categoryColumn, countColumn);

        placesByCategoryTable.setItems(placesCategoryData);
        reviewsByCategoryTable.setItems(reviewsCategoryData);
    }

    private void setupMap() {
        webView = new WebView();
        webEngine = webView.getEngine();
        mapContainer.getChildren().add(webView);

        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>خريطة الأماكن</title>
            <style>
                #map { height: 100%; width: 100%; border-radius: 8px; }
                body { margin: 0; padding: 0; }
                html, body, #map { height: 100%; }
            </style>
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([24.7136, 46.6753], 12); 
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; OpenStreetMap contributors'
                }).addTo(map);
                
                var markers = [];
                function addMarker(lat, lng, title, category, features) {
                    var marker = L.marker([lat, lng]).addTo(map);
                    var popupContent = "<b>" + title + "</b><br>" + "الفئة: " + category + "<br>";
                    if (features && features.length > 0) {
                        popupContent += "الميزات: " + features.join(", ");
                    }
                    marker.bindPopup(popupContent);
                    markers.push(marker);
                }
                function clearMarkers() {
                    markers.forEach(function(marker) { map.removeLayer(marker); });
                    markers = [];
                }
                function setView(lat, lng, zoom) { map.setView([lat, lng], zoom); }
            </script>
        </body>
        </html>
        """;

        webEngine.loadContent(htmlContent);

        // Ensure JS functions are ready before calling updateMapMarkers
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                updateMapMarkers();
            }
        });
    }

    @FXML
    private void refreshData() {
        loadDashboardData();
        loadCategoryStats();
        loadPlacesForMap();
    }

    private void loadDashboardData() {
        registeredUsersLabel.setText("120 حساب");
        totalReviewsLabel.setText("450 تعليق");
        totalPlacesLabel.setText("85 مكان");
        avgRatingLabel.setText("4.2");

        reviewsData.add(new ReviewResponseDTO("أحمد محمد", "مطعم الريان", "طعام لذيذ وخدمة ممتازة", 5, LocalDateTime.now().minusDays(2)));
        reviewsData.add(new ReviewResponseDTO("فاطمة علي", "مقهى النجوم", "جو هادئ وقهوة رائعة", 4, LocalDateTime.now().minusDays(1)));
        reviewsData.add(new ReviewResponseDTO("خالد سعيد", "متحف التراث", "تجربة ثقافية رائعة", 5, LocalDateTime.now().minusHours(5)));
    }

    private void loadCategoryStats() {
        placesCategoryData.add(new CategoryCount("مطاعم", 25));
        placesCategoryData.add(new CategoryCount("مقاهي", 18));
        placesCategoryData.add(new CategoryCount("متاحف", 12));

        reviewsCategoryData.add(new CategoryCount("مطاعم", 150));
        reviewsCategoryData.add(new CategoryCount("مقاهي", 120));
        reviewsCategoryData.add(new CategoryCount("متاحف", 80));
    }

    private void loadPlacesForMap() {
        placesData.clear();

        PlaceMapDTO place1 = new PlaceMapDTO(1, "مطعم الريان", "24.7136", "46.6753", "مطعم", "restaurant.jpg");
        place1.setAccessibilityFeatures(Arrays.asList("WHEELCHAIR_ACCESS", "BRAILLE_MENU"));

        PlaceMapDTO place2 = new PlaceMapDTO(2, "مقهى النجوم", "24.7236", "46.6853", "مقهى", "cafe.jpg");
        place2.setAccessibilityFeatures(Arrays.asList("WHEELCHAIR_ACCESS"));

        PlaceMapDTO place3 = new PlaceMapDTO(3, "متحف التراث", "24.7036", "46.6653", "متحف", "museum.jpg");
        place3.setAccessibilityFeatures(Arrays.asList("WHEELCHAIR_ACCESS", "AUDIO_GUIDE", "SIGN_LANGUAGE"));

        PlaceMapDTO place4 = new PlaceMapDTO(4, "سوق الحرف", "24.7336", "46.6953", "سوق", "market.jpg");
        place4.setAccessibilityFeatures(Arrays.asList("WHEELCHAIR_ACCESS", "BRAILLE_SIGNS"));

        PlaceMapDTO place5 = new PlaceMapDTO(5, "مطعم البحار", "24.6936", "46.6553", "مطعم", "seafood.jpg");
        place5.setAccessibilityFeatures(Arrays.asList("WHEELCHAIR_ACCESS", "LARGE_PRINT_MENU"));

        placesData.addAll(place1, place2, place3, place4, place5);

        updateMapMarkers();
    }

    private void updateMapMarkers() {
        if (webEngine == null) return;

        // Call only when JS is loaded
        if (webEngine.getLoadWorker().getState() != Worker.State.SUCCEEDED) return;

        webEngine.executeScript("clearMarkers();");

        for (PlaceMapDTO place : placesData) {
            try {
                double lat = Double.parseDouble(place.getLatitude());
                double lng = Double.parseDouble(place.getLongitude());

                String featuresJsArray = "[]";
                if (place.getAccessibilityFeatures() != null && !place.getAccessibilityFeatures().isEmpty()) {
                    featuresJsArray = "['" + String.join("','", place.getAccessibilityFeatures()) + "']";
                }

                String script = String.format(
                        "addMarker(%f, %f, '%s', '%s', %s);",
                        lat, lng, place.getPlaceName(), place.getCategory(), featuresJsArray
                );
                webEngine.executeScript(script);
            } catch (NumberFormatException e) {
                System.err.println("Invalid coordinates for place: " + place.getPlaceName());
            }
        }

        if (!placesData.isEmpty()) {
            try {
                PlaceMapDTO firstPlace = placesData.get(0);
                double lat = Double.parseDouble(firstPlace.getLatitude());
                double lng = Double.parseDouble(firstPlace.getLongitude());
                webEngine.executeScript(String.format("setView(%f, %f, 12);", lat, lng));
            } catch (NumberFormatException ignored) {}
        }
    }
}
