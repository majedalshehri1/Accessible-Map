package com.main.app.service.Survey;

import com.main.app.dto.Survey.SurveyRequestDTO;
import com.main.app.dto.Survey.SurveyResponseDTO;
import com.main.app.model.Survey.Survey;
import com.main.app.model.User.User;
import com.main.app.repository.Survey.SurveyRepository;
import com.main.app.repository.User.UserRepository;
import org.springframework.transaction.annotation.Transactional;
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


    @Transactional(readOnly = true)
    public List<SurveyResponseDTO> getAllSurvey() {
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
        if (surveyRepository.existsByUser_UserId(req.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "already submitted a survey"
            );
        }
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Survey survey = new Survey();
        survey.setUser(user);
        survey.setDescription(req.getDescription());
        survey.setRating(req.getRating());
        survey.setRead(false);

        Survey saved = surveyRepository.save(survey);

        return convertDTO(saved);
    }
    private SurveyResponseDTO convertDTO(Survey s) {
        if (s.getUser() == null) {
            throw new IllegalStateException("Survey " + s.getId() + " has no user");
        }
        SurveyResponseDTO dto = new SurveyResponseDTO();
        dto.setId(s.getId());
        dto.setUserId(s.getUser().getUserId());
        dto.setDescription(s.getDescription());
        dto.setRating(s.getRating());
        dto.setUserName(s.getUser().getUserName());
        dto.setRead(s.isRead());
        return dto;
    }
    public void deleteSurvey(Long id) {
        surveyRepository.deleteById(id);
    }

    @Transactional
    public void updateReadStatus(Long id, boolean read) {
        Survey s = getSurveyById(id);
        s.setRead(read);
        surveyRepository.save(s);
    }

    public boolean hasUserSubmittedSurvey(Long userId) {
        if (userId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "userId is required");
        }
        return surveyRepository.existsByUser_UserId(userId);
    }
}