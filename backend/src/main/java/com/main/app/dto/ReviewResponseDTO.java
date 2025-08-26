package com.main.app.dto;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String placeName;
    private Category placeCategory;
    private String userName;
    private String description;
    private Integer rating;
    private LocalDateTime reviewDate;

}