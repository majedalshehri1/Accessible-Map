package com.main.app.service;

import com.main.app.model.Place;
import com.main.app.model.Review;
import com.main.app.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findAllReviewByUserId(Long userId) {
        return reviewRepository.findAll();
    }

    public List<Review> findAllReviewByPlace(String place) {
        return reviewRepository.findAll();
    }

    public List<Review> findAllReviewRatingByPlace(String place) {
        return reviewRepository.findAll();
    }

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

}
