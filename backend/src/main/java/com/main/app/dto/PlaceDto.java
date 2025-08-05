package com.main.app.dto;


import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.model.Accessibility;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaceDto {

    @NotBlank(message = "Place name is required")
    private String placeName;

    @NotBlank(message = "longitude is required")
    private String longitude;

    @NotBlank(message = "latitude is required")
    private String latitude;

    private Category category;

    private AccessibillityType accessibility;
}
