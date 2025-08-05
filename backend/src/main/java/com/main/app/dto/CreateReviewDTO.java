package com.main.app.dto;

import lombok.Data;

@Data
public class CreateReviewDTO {
    private Long placeId;
    private String description;
    private Integer rating;
}
