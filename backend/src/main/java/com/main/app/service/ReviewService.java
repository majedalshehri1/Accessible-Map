package com.main.app.service;

import com.main.app.Enum.AccessibillityType;
import com.main.app.dto.*;
import com.main.app.model.*;
import com.main.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceFeatureRepository placeFeatureRepository;

    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO reviewDTO) {
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


    public List<ReviewResponseDTO> getReviewsByPlace(Long placeId) {
        return reviewRepository.findByPlaceId(placeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PlaceWithAccessibilityDTO> getPlacesWithAccessibility() {
        return placeRepository.findAll()
                .stream()
                .map(this::convertToPlaceDTO)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO convertToDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setPlaceName(review.getPlace().getPlaceName());
        dto.setPlaceCategory(review.getPlace().getPlaceCategory());
        dto.setUserName(review.getUser().getUserName());
        dto.setDescription(review.getDescription());
        dto.setRating(review.getRating());


        return dto;
    }

    private PlaceWithAccessibilityDTO convertToPlaceDTO(Place place) {
        PlaceWithAccessibilityDTO dto = new PlaceWithAccessibilityDTO();
        dto.setId(place.getId());
        dto.setPlaceName(place.getPlaceName());
        dto.setLongitude(place.getLongitude());
        dto.setLatitude(place.getLatitude());
        dto.setPlaceCategory(place.getPlaceCategory());

        // Get average rating
        Double averageRating = reviewRepository.findAverageRatingByPlace(place);
        dto.setAverageRating(averageRating != null ? averageRating : 0.0);

        // Get accessibility features
        List<AccessibillityType> features = placeFeatureRepository.findByPlace(place)
                .stream()
                .map(PlaceFeature::getAccessibillityType)
                .collect(Collectors.toList());
        dto.setAvailableFeatures(features);

        return dto;
    }
}