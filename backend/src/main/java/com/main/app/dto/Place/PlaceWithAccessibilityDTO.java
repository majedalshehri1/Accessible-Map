package com.main.app.dto.Place;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import lombok.Data;

import java.util.List;

@Data
public class PlaceWithAccessibilityDTO {
    private Long id;
    private String placeName;
    private Double longitude;
    private Double latitude;
    private Category placeCategory;
    private List<AccessibillityType> availableFeatures;
    private Double averageRating;
}