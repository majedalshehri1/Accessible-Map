package com.main.app.dto.Place;

import com.main.app.Enum.Category;

public interface PlaceWithStatsDTO {
    Long getId();
    String getPlaceName();
    Category getPlaceCategory();
    String getLatitude();
    String getLongitude();
    Double getAvgRating();
    Long getReviewCount();
}