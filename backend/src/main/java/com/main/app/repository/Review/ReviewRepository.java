package com.main.app.repository.Review;

import com.main.app.model.Place.Place;
import com.main.app.model.Review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPlaceId(Long placeId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place = :place")
    Double findAverageRatingByPlace(@Param("place") Place place);

    List<Review> findByUser_UserId(Long userId);

    Page<Review> findAll(Pageable pageable);
    Page<Review> findByPlace_Id(Long placeId, Pageable pageable);
    Page<Review> findByUser_UserId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM Review r " +
            "WHERE r.review_date >= NOW() - INTERVAL '24 HOURS'",
            nativeQuery = true)
    List<Review> findAllFromLast24Hours();

    @Query("""
           SELECT p.placeCategory AS category, COUNT(r) AS count
           FROM Review r
           JOIN r.place p
           GROUP BY p.placeCategory
           """)
    List<Object[]> countReviewsByCategory();
}