package com.wakeb.yusradmin.models;

import com.wakeb.yusradmin.utils.AccessibilityFeatures;
import com.wakeb.yusradmin.utils.CATEGORY;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;


@NoArgsConstructor
public class Place {
    public long id;
    public String placeName;
    public String longitude;
    public String latitude;
    public CATEGORY category;
    public AccessibilityFeatures[] accessibilityFeatures;
    public String imageUrl;

    public Place(long id, String placeName, String longitude, String latitude, CATEGORY category, AccessibilityFeatures[] accessibilityFeatures, String imageUrl) {
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

    public CATEGORY getCategory() {
        return category;
    }

    public void setCategory(CATEGORY category) {
        this.category = category;
    }

    public AccessibilityFeatures[] getAccessibilityFeatures() {
        return accessibilityFeatures;
    }

    public void setAccessibilityFeatures(AccessibilityFeatures[] accessibilityFeatures) {
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
