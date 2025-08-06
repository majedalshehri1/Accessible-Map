package com.main.app.dto;

import com.main.app.Enum.AccessibillityType;
import lombok.Data;

import java.util.List;

@Data
public class ReviewRequestDTO {
    private Long placeId;
    private Long userId;
    private String description;
    private Integer rating;
}
