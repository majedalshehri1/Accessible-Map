package com.wakeb.yusradmin.models;

import java.util.Arrays;

public class Place {
    public long id;
    public String placeName;
    public String longitude;
    public String latitude;
    public String category;
    public String[] accessibilityFeatures;
    public String imageUrl;

    public Place(long id, String placeName, String longitude, String latitude, String category, String[] accessibilityFeatures, String imageUrl) {
        this.id = id;
        this.placeName = placeName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.category = category;
        this.accessibilityFeatures = accessibilityFeatures;
        this.imageUrl = imageUrl;
    }

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

    public String[] getAccessibilityFeatures() {
        return accessibilityFeatures;
    }

    public void setAccessibilityFeatures(String[] accessibilityFeatures) {
        this.accessibilityFeatures = accessibilityFeatures;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", placeName='" + placeName + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", category='" + category + '\'' +
                ", accessibilityFeatures=" + Arrays.toString(accessibilityFeatures) +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
