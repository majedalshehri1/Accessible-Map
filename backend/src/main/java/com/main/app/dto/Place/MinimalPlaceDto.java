package com.main.app.dto.Place;

import com.main.app.Enum.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MinimalPlaceDto {
    private long id ;
    @NotBlank private String placeName;
    @NotBlank private String longitude;
    @NotBlank private String latitude;
    private Category category;
}
