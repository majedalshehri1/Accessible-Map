package com.main.app.service.Review;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Action;
import com.main.app.Enum.EntityType;
import com.main.app.config.AuthUser;
import com.main.app.config.SecurityUtils;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.Place.PlaceWithAccessibilityDTO;
import com.main.app.dto.Review.ReviewRequestDTO;
import com.main.app.dto.Review.ReviewResponseDTO;
import com.main.app.model.Place.Place;
import com.main.app.model.Place.PlaceFeature;
import com.main.app.model.Review.Review;
import com.main.app.model.User.User;
import com.main.app.repository.Place.PlaceFeatureRepository;
import com.main.app.repository.Place.PlaceRepository;
import com.main.app.repository.Review.ReviewRepository;
import com.main.app.repository.User.UserRepository;
import com.main.app.service.Admin.AdminLogService;
import com.main.app.service.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceFeatureRepository placeFeatureRepository;

    @Autowired
    JwtService jwtService;
    @Autowired
    AdminLogService adminLogService;

    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO reviewDTO) {
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Place place = placeRepository.findById(reviewDTO.getPlaceId())
                .orElseThrow(() -> new RuntimeException("Place not found"));

        Review review = new Review();
        review.setPlace(place);
        review.setUser(user);
        review.setDescription(reviewDTO.getDescription());
        review.setRating(reviewDTO.getRating());

        Review saved = reviewRepository.save(review);

        var me = SecurityUtils.currentUser();
        Long   actorId = me.map(AuthUser::id).orElse(null);
        String actorName =me.map(AuthUser::name).orElse(null) ;

        adminLogService.writeLog(
                EntityType.REVIEW,
                Action.CREATE,
                saved.getId(),
                actorId,
                actorName,
                "تم اضافة تقييم : " + "'"+saved.getDescription()+"'" +" وتقييمه : " + saved.getRating()
        );

        return convertToDTO(saved);
    }


    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewById(long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        return convertToDTO(review);
    }


    public PaginatedResponse<ReviewResponseDTO> getAllReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findAll(pageable);

        List<ReviewResponseDTO> reviewDtos = reviewPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                reviewDtos,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }

    public PaginatedResponse<ReviewResponseDTO> getReviewsByPlace(Long placeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByPlace_Id(placeId, pageable);

        List<ReviewResponseDTO> reviewDtos = reviewPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                reviewDtos,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }

    public PaginatedResponse<ReviewResponseDTO> getReviewsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByUser_UserId(userId, pageable);

        List<ReviewResponseDTO> reviewDtos = reviewPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                reviewDtos,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }

    public List<PlaceWithAccessibilityDTO> getPlacesWithAccessibility() {
        return placeRepository.findAll()
                .stream()
                .map(this::convertToPlaceDTO)
                .collect(Collectors.toList());
    }

    public ReviewResponseDTO convertToDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setPlaceName(review.getPlace().getPlaceName());
        dto.setPlaceCategory(review.getPlace().getPlaceCategory());
        dto.setUserName(review.getUser().getUserName());
        dto.setDescription(review.getDescription());
        dto.setRating(review.getRating());
        dto.setReviewDate(review.getReviewDate());

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
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);

        var me = SecurityUtils.currentUser();
        Long   actorId = me.map(AuthUser::id).orElse(null);
        String actorName =me.map(AuthUser::name).orElse(null) ;

        adminLogService.writeLog(
                EntityType.REVIEW,
                Action.DELETE,
                reviewId,
                actorId,
                actorName,
                "تم حذف تقييم"
        );
    }
    public ReviewResponseDTO editReview(Long reviewId, ReviewRequestDTO reviewDTO) {
        Review review =  reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
//        if (!review.getUser().getUserId().equals(review.getUser().getUserId())) {
//            throw new RuntimeException("Not authorized to edit review");
//        }
        var oldDes = review.getDescription();
        var oldRating = review.getRating();
        review.setDescription(reviewDTO.getDescription());
        review.setRating(reviewDTO.getRating());
        Review updatedReview = reviewRepository.save(review);

        var me = SecurityUtils.currentUser();
        Long   actorId = me.map(AuthUser::id).orElse(null);
        String actorName =me.map(AuthUser::name).orElse(null) ;

        adminLogService.writeLog(
                EntityType.REVIEW,
                Action.UPDATE,
                reviewId,
                actorId,
                actorName,
                String.format("%s %s %s %s ", " تم تعديل الوصف من :","'"+oldDes+"'" +"وتقييمه: " + oldRating ," الى :","'"+review.getDescription())+"'"+"وتقييمه:"+ review.getRating()
        );
        return convertToDTO(updatedReview);
    }

    public List<ReviewResponseDTO> getLast24HoursReviews() {
        List<Review> reviews = reviewRepository.findAllFromLast24Hours();
        return reviews.stream()
                .map(this::convertToDTO)
                .toList();

    }

    public List<Object[]> getReviewCountByCategory() {
        return reviewRepository.countReviewsByCategory();

    }
}

