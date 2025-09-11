package com.wakeb.yusradmin.controllers;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;

import com.wakeb.yusradmin.dto.PlaceMapDTO;
import com.wakeb.yusradmin.mappers.PlaceMappers;
import com.wakeb.yusradmin.models.PaginatedResponse;
import com.wakeb.yusradmin.models.Place;
import com.wakeb.yusradmin.services.PlaceService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.*;
import java.util.stream.Collectors;

public class MapController {

    @FXML private MapView mapView;
    @FXML private Label latLabel, lngLabel;

    private final List<Marker> markers = new ArrayList<>();
    private final Map<Marker, MapLabel> infoLabels = new HashMap<>();

    private final PlaceService placeService = new PlaceService();

    @FXML
    private void initialize() {
        mapView.initialize();

        mapView.initializedProperty().addListener((obs, was, is) -> {
            if (!is) return;

            Coordinate riyadh = new Coordinate(24.7136, 46.6753);
            mapView.setCenter(riyadh);
            mapView.setZoom(12);

            refreshFromDatabase(); // first load
        });

        mapView.addEventHandler(MapViewEvent.MAP_POINTER_MOVED, e -> {
            Coordinate c = e.getCoordinate();
            if (c != null) {
                latLabel.setText(String.format("%.6f", c.getLatitude()));
                lngLabel.setText(String.format("%.6f", c.getLongitude()));
            }
        });

        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, e -> {
            Marker m = e.getMarker();
            if (m == null) return;

            infoLabels.values().forEach(lbl -> lbl.setVisible(false));

            MapLabel label = infoLabels.get(m);
            if (label != null) {
                label.setPosition(m.getPosition());
                label.setVisible(true);
            }
        });
    }


    @FXML
    public void refreshFromDatabase() {
        Task<PaginatedResponse<Place>> task = placeService.getAllPlaces(0, 1000);

        task.setOnSucceeded(ev -> {
            PaginatedResponse<Place> response = task.getValue();
            if (response == null) {
                Platform.runLater(() -> {
                    clearPins();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Empty response from server");
                    alert.show();
                });
                return;
            }

            List<PlaceMapDTO> dtoList = response.getContent().stream()
                    .map(PlaceMappers::toMapDTO)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());

            Platform.runLater(() -> renderPlaces(dtoList));
        });

        task.setOnFailed(ev -> {
            Throwable ex = task.getException();
            Platform.runLater(() -> {
                clearPins();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Load Places");
                alert.setHeaderText("Failed to load places");
                alert.setContentText(ex != null ? ex.getMessage() : "Unknown error");
                alert.show();
            });
        });

        new Thread(task, "getAllPlaces").start();
    }




    private void renderPlaces(List<PlaceMapDTO> places) {
        clearPins();
        for (PlaceMapDTO p : places) {
            Coordinate c = new Coordinate(p.getLat(), p.getLng());
            addPin(c, p.getName(), p.getCategory());
        }
        fitToMarkers();
    }


    private void addPin(Coordinate c, String name, String category) {
        Marker marker = Marker.createProvided(Marker.Provided.BLUE)
                .setPosition(c)
                .setVisible(true);

        markers.add(marker);
        mapView.addMarker(marker);

        MapLabel label = new MapLabel(name + " • " + category)
                .setPosition(c)
                .setCssClass("map-label")
                .setVisible(false);

        mapView.addLabel(label);
        infoLabels.put(marker, label);
    }


    private void fitToMarkers() {
        if (markers.isEmpty()) return;

        double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY;
        double minLng = Double.POSITIVE_INFINITY, maxLng = Double.NEGATIVE_INFINITY;

        for (Marker m : markers) {
            Coordinate c = m.getPosition();
            minLat = Math.min(minLat, c.getLatitude());
            maxLat = Math.max(maxLat, c.getLatitude());
            minLng = Math.min(minLng, c.getLongitude());
            maxLng = Math.max(maxLng, c.getLongitude());
        }

        Coordinate upperLeft  = new Coordinate(maxLat, minLng);
        Coordinate lowerRight = new Coordinate(minLat, maxLng);
        mapView.setExtent(Extent.forCoordinates(upperLeft, lowerRight)); // MapJFX 3.1.0
    }


    // more button actions
    @FXML
    private void addPinAtCenter() {
        Coordinate center = mapView.getCenter();
        if (center != null) addPin(center, "Custom", "Center");
    }

    @FXML
    private void clearPins() {
        infoLabels.values().forEach(label -> { label.setVisible(false); mapView.removeLabel(label); });
        infoLabels.clear();

        markers.forEach(mapView::removeMarker);
        markers.clear();
    }
}
