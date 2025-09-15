package com.main.app.dto.Place;


import com.main.app.Enum.Category;
import com.main.app.dto.Review.ReviewResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlaceDto {
    private long id ;
    @NotBlank private String placeName;
    @NotBlank private Double longitude;
    @NotBlank private Double latitude;
    private Category category;
    private List<ReviewResponseDTO> reviews;
    private List<String> accessibilityFeatures = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
}
