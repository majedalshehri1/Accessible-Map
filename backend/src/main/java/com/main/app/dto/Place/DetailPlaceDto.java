package com.main.app.dto.Place;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.dto.Review.ReviewResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class DetailPlaceDto {
    Long id;
    String placeName;
    Category placeCategory;
    Double latitude;
    Double longitude;
    List<AccessibillityType> features;
    List<String> images;
    List<ReviewResponseDTO> reviews;
    Double avgRating;
    Long reviewsCount;
}
