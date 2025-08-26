package com.wakeb.yusradmin.dto;

import java.util.ArrayList;
import java.util.List;

public class PlaceMapDTO {
    private long id;
    private String placeName;
    private String longitude;
    private String latitude;
    private String category; // Using String instead of Enum for simplicity
    private List<String> accessibilityFeatures = new ArrayList<>();
    private String imageUrl;

    public PlaceMapDTO() {
    }

    public PlaceMapDTO(long id, String placeName, String longitude, String latitude, String category, String imageUrl) {
        this.id = id;
        this.placeName = placeName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getAccessibilityFeatures() {
        return accessibilityFeatures;
    }

    public void setAccessibilityFeatures(List<String> accessibilityFeatures) {
        this.accessibilityFeatures = accessibilityFeatures;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}