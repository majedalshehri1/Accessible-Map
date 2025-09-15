package com.main.app.service.Admin;

import com.main.app.dto.Place.PlaceDto;
import com.main.app.dto.Place.PlaceUpdatedDto;
import com.main.app.dto.Place.TopPlaceDto;
import com.main.app.dto.Review.ReviewRequestDTO;
import com.main.app.dto.User.UserDto;
import com.main.app.model.Place.Place;
import com.main.app.model.Review.Review;
import com.main.app.model.User.User;
import com.main.app.repository.Place.PlaceRepository;
import com.main.app.repository.Review.ReviewRepository;
import com.main.app.repository.User.UserRepository;
import com.main.app.service.Place.PlaceService;
import com.main.app.service.Review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private final PlaceService placeService;

    @Autowired
    private ReviewService reviewService;

    public AdminService(PlaceService placeService) {
        this.placeService = placeService;
    }


    public Review adminUpdateReview(Long reviewId , ReviewRequestDTO dto){
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (dto.getDescription() != null) r.setDescription(dto.getDescription());
        if (dto.getRating() != null) r.setRating(dto.getRating());
        return reviewRepository.save(r);
    }

    public Place adminUpdatePlace(Long id, PlaceUpdatedDto dto) {
        Place place = placeService.getPlaceOrThrow(id);

        place.setPlaceName(dto.getPlaceName());
        place.setPlaceCategory(dto.getCategory());

        return placeService.updatePlace(place);
    }


    public User adminUpdateUser(Long userId , UserDto dto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUserName(dto.getUserName());
        user.setUserEmail(dto.getUserEmail());
        user.setUserRole(dto.getHasRole());
        user.setIsBlocked(dto.getIsBlocked());

        return userRepository.save(user);
    }


    public void deleteReview(Long reviewId){
        reviewRepository.deleteById(reviewId);
    }
    public void deletePlace(Long placeId){
        placeRepository.deleteById(placeId);
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
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

    public List<User> searchUserByEmail(String email){
        if(email.isEmpty())
            throw new UsernameNotFoundException(email);

        return userRepository.searchByUserEmailContainingIgnoreCase(email);
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

