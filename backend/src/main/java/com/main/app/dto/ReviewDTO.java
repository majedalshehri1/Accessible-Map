package com.main.app.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long reviewId;
    private Long placeId;
    private Long userId;
    private String userName;
    private String description;
    private Integer rating;
}

