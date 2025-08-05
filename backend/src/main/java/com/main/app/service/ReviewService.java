package com.main.app.service;

import com.main.app.dto.ReviewDTO;
import com.main.app.dto.CreateReviewDTO;
import com.main.app.model.Place;
import com.main.app.model.Review;
import com.main.app.model.User;
import com.main.app.repository.PlaceRepository;
import com.main.app.repository.ReviewRepository;
import com.main.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public ReviewDTO createReview(CreateReviewDTO reviewDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userRepository.findByUserEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Place place = placeRepository.findById(reviewDTO.getPlaceId())
                .orElseThrow(() -> new RuntimeException("Place not found"));

        Review review = new Review();
        review.setPlace(place);
        review.setUser(user);
        review.setDescription(reviewDTO.getDescription());
        review.setRating(reviewDTO.getRating());

        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    public List<ReviewDTO> getReviewsByPlace(Long placeId) {
        return reviewRepository.findByPlace_PlaceId(placeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setPlaceId(review.getPlace().getPlaceId());
        dto.setUserId(review.getUser().getUserId());
        dto.setUserName(review.getUser().getUserName()); // Show who posted it
        dto.setDescription(review.getDescription());
        dto.setRating(review.getRating());
        return dto;
    }
}