package com.wakeb.yusradmin.dto;

import com.wakeb.yusradmin.utils.CATEGORY;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceUpdateDto {
    private String placeName;
    private CATEGORY category;

    public PlaceUpdateDto(String placeName, CATEGORY category) {
        this.placeName = placeName;
        this.category = category;

    }
}