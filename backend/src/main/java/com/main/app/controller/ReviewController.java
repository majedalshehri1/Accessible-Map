package com.main.app.controller;

import com.main.app.dto.Review.ReviewRequestDTO;
import com.main.app.dto.Review.ReviewResponseDTO;
import com.main.app.service.Review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

//    @PostMapping("/create")
//    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO reviewDTO) {
//        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
//    }

    @PostMapping("/create")
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByPlace(@PathVariable Long placeId) {
        return ResponseEntity.ok(reviewService.getReviewsByPlace(placeId));
    }
    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUserId(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }
    @DeleteMapping("/delete/{review_id}")
    public ResponseEntity<ReviewResponseDTO> deleteReview(@PathVariable("review_id") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/edit/{review_id}")
    public ResponseEntity<ReviewResponseDTO> editReview(@PathVariable("review_id") Long id, @RequestBody ReviewRequestDTO reviewDTO) {
        ReviewResponseDTO reviewResponseDTO = reviewService.editReview(id, reviewDTO);
        return ResponseEntity.ok().body(reviewResponseDTO);
    }
//    @GetMapping("/last24hours")
//    public List<ReviewResponseDTO> getReviewsFromLast24Hours() {
//        return reviewService.getLast24HoursReviews();
//    }

}
