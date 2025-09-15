package com.main.app.dto;

import lombok.Data;

@Data
public class SurveyRequestDTO {
    private Long userId;
    private String description;
    private Integer rating;

}
