package com.wakeb.yusradmin.models;

import com.wakeb.yusradmin.utils.AccessibilityFeatures;
import com.wakeb.yusradmin.utils.CATEGORY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Place {
    private long id;
    private String placeName;
    private String longitude;
    private String latitude;
    private CATEGORY category;
    private AccessibilityFeatures[] accessibilityFeatures;

    private List<String> imageUrls = new ArrayList<>();

    public Place() {}

    public Place(long id, String placeName, String longitude, String latitude,
                 CATEGORY category, AccessibilityFeatures[] accessibilityFeatures,
                 List<String> imageUrls) {
        this.id = id;
        this.placeName = placeName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.category = category;
        this.accessibilityFeatures = accessibilityFeatures;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
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

    // --- Multiple Image URLs ---
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    /**
     * Convenience method to get the first image (for display in cards).
     */
    public String getFirstImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return null;
    }


    public void setImageUrl(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            if (this.imageUrls == null) {
                this.imageUrls = new ArrayList<>();
            }
            this.imageUrls.clear();
            this.imageUrls.add(imageUrl);
        }

    }
    }