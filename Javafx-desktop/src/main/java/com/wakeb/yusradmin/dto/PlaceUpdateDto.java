package com.wakeb.yusradmin.dto;

public class PlaceUpdateDto {
    public long id;
    public String placeName;
    public String category;

    public PlaceUpdateDto(long id, String placeName, String category) {
        this.id = id;
        this.placeName = placeName;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
