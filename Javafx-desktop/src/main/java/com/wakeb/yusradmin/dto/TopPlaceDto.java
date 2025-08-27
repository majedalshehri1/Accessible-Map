package com.wakeb.yusradmin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TopPlaceDto {
    private Long id;
    private String placeName;
    private String placeCategory;
    private Double avgRating;
    private Long reviewCount;

    public TopPlaceDto() {}

    public TopPlaceDto(Long id, String placeName, String placeCategory, Double avgRating, Long reviewCount) {
        this.id = id;
        this.placeName = placeName;
        this.placeCategory = placeCategory;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }

    // Remove all @JsonProperty annotations from getters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public String getPlaceCategory() { return placeCategory; }
    public void setPlaceCategory(String placeCategory) { this.placeCategory = placeCategory; }

    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    public Long getReviewCount() { return reviewCount; }
    public void setReviewCount(Long reviewCount) { this.reviewCount = reviewCount; }
}