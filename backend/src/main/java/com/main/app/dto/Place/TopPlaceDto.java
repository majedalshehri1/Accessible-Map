package com.main.app.dto.Place;

import com.main.app.Enum.Category;


public interface TopPlaceDto {
    Long getId();
    String getPlaceName();
    Category getPlaceCategory();
    Double getAvgRating();
    Long getReviewCount();
}
