package com.main.app.dto;


import com.main.app.Enum.Category;
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
}
