package com.main.app.dto.Review;

import com.main.app.Enum.Category;
import lombok.Data;

import java.time.LocalDateTime;

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