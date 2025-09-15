package com.main.app.dto.Place;

import com.main.app.Enum.Category;
import lombok.Data;

@Data
public class PlaceUpdatedDto {
    private String placeName;
    private Category category;
}
