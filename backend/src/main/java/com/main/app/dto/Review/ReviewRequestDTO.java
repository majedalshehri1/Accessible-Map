package com.main.app.dto.Review;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Long placeId;
    private Long userId;
    private String description;
    private Integer rating;
}
