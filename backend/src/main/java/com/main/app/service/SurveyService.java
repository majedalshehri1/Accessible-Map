package com.main.app.service;

import com.main.app.dto.SurveyRequestDTO;
import com.main.app.dto.SurveyResponseDTO;
import com.main.app.model.Survey;
import com.main.app.model.User;
import com.main.app.repository.SurveyRepository;
import com.main.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SurveyService {
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private UserRepository userRepository;

    public List<SurveyResponseDTO> getAllSurvey(){
        return surveyRepository.findAll().stream().map(this::convertDTO).toList();
    }

    public Survey getSurveyById(long id){
        return surveyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));
    }

    public SurveyResponseDTO createSurvey(SurveyRequestDTO req) {
        if (req.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        if (req.getRating() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating is required");
        }

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Survey survey = new Survey();
        survey.setUser(user);
        survey.setDescription(req.getDescription());
        survey.setRating(req.getRating());

        Survey saved = surveyRepository.save(survey);

        return convertDTO(saved);
    }
    private SurveyResponseDTO convertDTO(Survey s) {
        SurveyResponseDTO dto = new SurveyResponseDTO();
        dto.setId(s.getId());
        dto.setUserId(s.getUser().getUserId());
        dto.setDescription(s.getDescription());
        dto.setRating(s.getRating());
        dto.setUserName(s.getUser().getUserName());
        return dto;
    }
    public void deleteSurvey(Long id) {
        surveyRepository.deleteById(id);
    }
}