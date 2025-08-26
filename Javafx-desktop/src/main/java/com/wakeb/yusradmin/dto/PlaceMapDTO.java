// PlaceMapDTO.java
package com.wakeb.yusradmin.dto;

import java.util.List;

public class PlaceMapDTO {
    private Long id;
    private String name; // This should match your PlaceDto's 'name' field
    private String latitude;
    private String longitude;
    private String category;
    private String imageUrl;
    private List<String> accessibilityFeatures;

    // Constructors, getters, and setters
    public PlaceMapDTO() {}

    public PlaceMapDTO(Long id, String name, String latitude, String longitude,
                       String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Add getPlaceName() method to match what your map expects
    public String getPlaceName() {
        return name;
    }

    // Getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getAccessibilityFeatures() { return accessibilityFeatures; }
    public void setAccessibilityFeatures(List<String> accessibilityFeatures) {
        this.accessibilityFeatures = accessibilityFeatures;
    }
}