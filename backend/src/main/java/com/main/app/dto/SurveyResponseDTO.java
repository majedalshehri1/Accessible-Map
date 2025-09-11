package com.main.app.dto;

import lombok.Data;

@Data
public class SurveyResponseDTO {
    private Long Id;
    private Long userId;
    private String description;
    private Integer rating;


}
