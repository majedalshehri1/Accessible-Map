package com.main.app.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    HOSPITAL,
    COFFEE,
    RESTAURANT,
    PARK,
    MALL;

    @JsonCreator
    public static Category fromString(String value) {
        return Category.valueOf(value.toUpperCase());
    }
}
