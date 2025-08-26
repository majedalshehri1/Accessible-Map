package com.wakeb.yusradmin.controllers;

import com.wakeb.yusradmin.dto.PlaceMapDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    @FXML
    private WebView mapWebView;

    private WebEngine webEngine;
    private final ObservableList<PlaceMapDTO> placesData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupMap();
        loadSamplePlaces();
    }

    private void setupMap() {
        webEngine = mapWebView.getEngine();

        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>خريطة الأماكن</title>
            <style>
                #map { 
                    height: 100%; 
                    width: 100%; 
                    border-radius: 8px;
                }
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
                    var popupContent = "<b>" + title + "</b><br>" + 
                                      "الفئة: " + category + "<br>";
                    
                    if (features && features.length > 0) {
                        popupContent += "الميزات: " + features.join(", ");
                    }
                    
                    marker.bindPopup(popupContent);
                    markers.push(marker);
                }
                
                function clearMarkers() {
                    markers.forEach(function(marker) {
                        map.removeLayer(marker);
                    });
                    markers = [];
                }
                
                function setView(lat, lng, zoom) {
                    map.setView([lat, lng], zoom);
                }
                
                // Make functions globally available
                window.mapFunctions = {
                    addMarker: addMarker,
                    clearMarkers: clearMarkers,
                    setView: setView
                };
            </script>
        </body>
        </html>
        """;

        webEngine.loadContent(htmlContent);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                updateMapMarkers();
            }
        });
    }

    private void loadSamplePlaces() {
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
    }

    public void updateMapMarkers() {
        if (webEngine == null) return;

        if (webEngine.getLoadWorker().getState() != Worker.State.SUCCEEDED) return;

        // Use the global function
        webEngine.executeScript("if (window.mapFunctions) window.mapFunctions.clearMarkers();");

        for (PlaceMapDTO place : placesData) {
            try {
                double lat = Double.parseDouble(place.getLatitude());
                double lng = Double.parseDouble(place.getLongitude());

                String featuresJsArray = "[]";
                if (place.getAccessibilityFeatures() != null && !place.getAccessibilityFeatures().isEmpty()) {
                    featuresJsArray = "['" + String.join("','", place.getAccessibilityFeatures()) + "']";
                }

                String script = String.format(
                        "if (window.mapFunctions) window.mapFunctions.addMarker(%f, %f, '%s', '%s', %s);",
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
                webEngine.executeScript(String.format(
                        "if (window.mapFunctions) window.mapFunctions.setView(%f, %f, 12);", lat, lng
                ));
            } catch (NumberFormatException ignored) {}
        }
    }

    // Public method to update places from other controllers
    public void setPlacesData(java.util.List<PlaceMapDTO> places) {
        placesData.setAll(places);
        updateMapMarkers();
    }

    // Public method to refresh map
    public void refreshMap() {
        updateMapMarkers();
    }
}