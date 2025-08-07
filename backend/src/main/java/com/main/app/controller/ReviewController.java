package com.main.app.controller;

import com.main.app.dto.*;
import com.main.app.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/create")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByPlace(@PathVariable Long placeId) {
        return ResponseEntity.ok(reviewService.getReviewsByPlace(placeId));
    }
    @DeleteMapping("/delete/{place_id}")
    public ResponseEntity<ReviewResponseDTO> deleteReview(@PathVariable Long placeId) {
        reviewService.deleteReview(placeId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/edit")
    public ResponseEntity<ReviewResponseDTO> editReview(@PathVariable Long id, @RequestBody ReviewRequestDTO reviewDTO) {
        ReviewResponseDTO reviewResponseDTO = reviewService.editReview(id, reviewDTO);
        return ResponseEntity.ok().body(reviewResponseDTO);
    }

}
