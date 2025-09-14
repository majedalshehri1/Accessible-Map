package com.main.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.app.dto.Review.ReviewRequestDTO;
import com.main.app.dto.Review.ReviewResponseDTO;
import com.main.app.service.Review.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReview_ShouldReturnCreatedReview() throws Exception {

        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setDescription("Amazing place!");

        ReviewResponseDTO response = new ReviewResponseDTO();
        response.setId(1L);
        response.setDescription("Amazing place!");

        when(reviewService.createReview(any(ReviewRequestDTO.class))).thenReturn(response);


        mockMvc.perform(post("/api/reviews/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Amazing place!"));
    }

}
