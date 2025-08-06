package com.main.app.dto;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import lombok.Data;

import java.util.List;

@Data
public class PlaceWithAccessibilityDTO {
    private Long id;
    private String placeName;
    private String longitude;
    private String latitude;
    private Category placeCategory;
    private List<AccessibillityType> availableFeatures;
    private Double averageRating;
}