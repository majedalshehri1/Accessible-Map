package com.main.app.dto.Place;

import com.main.app.Enum.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MinimalPlaceDto {
    private long id ;
    @NotBlank private String placeName;
    @NotBlank private Double longitude;
    @NotBlank private Double latitude;
    private Category category;
}
