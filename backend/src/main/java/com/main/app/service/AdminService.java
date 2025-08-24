package com.main.app.service;

import com.main.app.dto.PlaceDto;
import com.main.app.dto.ReviewRequestDTO;
import com.main.app.dto.ReviewResponseDTO;
import com.main.app.dto.TopPlaceDto;
import com.main.app.model.Place;
import com.main.app.model.Review;
import com.main.app.model.User;
import com.main.app.repository.PlaceRepository;
import com.main.app.repository.ReviewRepository;
import com.main.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ReviewService reviewService;



    public Review adminUpdateReview(Long reviewId , ReviewRequestDTO dto){
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (dto.getDescription() != null) r.setDescription(dto.getDescription());
        if (dto.getRating() != null) r.setRating(dto.getRating());
        return reviewRepository.save(r);
    }

    public Place adminUpdatePlace(Long placeId , PlaceDto dto){
        Place place =  placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));
         place.setPlaceName(dto.getPlaceName());
         place.setLongitude(dto.getLongitude());
         place.setLatitude(dto.getLatitude());
         place.setPlaceCategory(dto.getCategory());

        return placeRepository.save(place);
    }

    public void deleteReview(Long reviewId){
        reviewRepository.deleteById(reviewId);
    }
    public void deletePlace(Long placeId){
        placeRepository.deleteById(placeId);
    }

    public long countAllPlaces(){
        return placeRepository.count();
    }
    public long countAllReviews(){
        return reviewRepository.count();
    }
    public long countAllUsers(){
        return userRepository.count();
    }

    public List<TopPlaceDto> getTopPlaces(int limit){

        Pageable page =  PageRequest.of(0, limit);
        return placeRepository.findTopPlaces(page);
    }
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsBlocked(true);
        userRepository.save(user);
    }

    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsBlocked(false);
        userRepository.save(user);
    }


}

