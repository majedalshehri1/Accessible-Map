package com.main.app.dto;


import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlaceDto {
    @NotBlank private String placeName;
    @NotBlank private String longitude;
    @NotBlank private String latitude;
    private Category category;
    private List<String> accessibilityFeatures = new ArrayList<>();
    private String imageUrl;
}
