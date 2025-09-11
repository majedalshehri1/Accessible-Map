package com.main.app.controller;

import com.main.app.dto.SurveyRequestDTO;
import com.main.app.dto.SurveyResponseDTO;
import com.main.app.model.Survey;
import com.main.app.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {
    @Autowired
    private SurveyService surveyService;

    //todo get all and delete survey in Admin controller --->


    @PostMapping("/create")
    public ResponseEntity<SurveyResponseDTO> createSurvey(@RequestBody SurveyRequestDTO body) {
        SurveyResponseDTO created = surveyService.createSurvey(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
