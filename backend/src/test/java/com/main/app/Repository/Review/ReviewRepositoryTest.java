package com.main.app.Repository.Review;

import com.main.app.Enum.Category;
import com.main.app.model.Place.Place;
import com.main.app.model.Review.Review;
import com.main.app.model.User.User;
import com.main.app.repository.Review.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private com.main.app.repository.Place.PlaceRepository placeRepository;

    @Autowired
    private com.main.app.repository.User.UserRepository userRepository;

    @Test
    void findByPlaceId_WhenPlaceHasReviews_ShouldReturnReviews() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        Review review1 = new Review();
        review1.setPlace(savedPlace);
        review1.setUser(savedUser);
        review1.setDescription("Great place!");
        review1.setRating(5);
        review1.setReviewDate(LocalDateTime.now());

        Review review2 = new Review();
        review2.setPlace(savedPlace);
        review2.setUser(savedUser);
        review2.setDescription("Nice atmosphere");
        review2.setRating(4);
        review2.setReviewDate(LocalDateTime.now());

        reviewRepository.saveAll(List.of(review1, review2));

        // When
        List<Review> result = reviewRepository.findByPlaceId(savedPlace.getId());

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getPlace().getId().equals(savedPlace.getId())));
    }

    @Test
    void findByPlaceId_WhenPlaceHasNoReviews_ShouldReturnEmptyList() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        // When
        List<Review> result = reviewRepository.findByPlaceId(savedPlace.getId());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAverageRatingByPlace_WhenPlaceHasReviews_ShouldReturnAverage() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        Review review1 = new Review();
        review1.setPlace(savedPlace);
        review1.setUser(savedUser);
        review1.setDescription("Great place!");
        review1.setRating(5);
        review1.setReviewDate(LocalDateTime.now());

        Review review2 = new Review();
        review2.setPlace(savedPlace);
        review2.setUser(savedUser);
        review2.setDescription("Nice atmosphere");
        review2.setRating(3);
        review2.setReviewDate(LocalDateTime.now());

        reviewRepository.saveAll(List.of(review1, review2));

        // When
        Double averageRating = reviewRepository.findAverageRatingByPlace(savedPlace);

        // Then
        assertNotNull(averageRating);
        assertEquals(4.0, averageRating, 0.01);
    }

    @Test
    void findAverageRatingByPlace_WhenPlaceHasNoReviews_ShouldReturnNull() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        // When
        Double averageRating = reviewRepository.findAverageRatingByPlace(savedPlace);

        // Then
        assertNull(averageRating);
    }

    @Test
    void findByUser_UserId_WhenUserHasReviews_ShouldReturnReviews() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        Review review1 = new Review();
        review1.setPlace(savedPlace);
        review1.setUser(savedUser);
        review1.setDescription("Great place!");
        review1.setRating(5);
        review1.setReviewDate(LocalDateTime.now());

        Review review2 = new Review();
        review2.setPlace(savedPlace);
        review2.setUser(savedUser);
        review2.setDescription("Nice atmosphere");
        review2.setRating(4);
        review2.setReviewDate(LocalDateTime.now());

        reviewRepository.saveAll(List.of(review1, review2));

        // When
        List<Review> result = reviewRepository.findByUser_UserId(savedUser.getUserId());

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getUser().getUserId().equals(savedUser.getUserId())));
    }

    @Test
    void findByUser_UserId_WhenUserHasNoReviews_ShouldReturnEmptyList() {
        // Given
        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        // When
        List<Review> result = reviewRepository.findByUser_UserId(savedUser.getUserId());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_WithPageable_ShouldReturnPaginatedResults() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        for (int i = 1; i <= 15; i++) {
            Review review = new Review();
            review.setPlace(savedPlace);
            review.setUser(savedUser);
            review.setDescription("Review " + i);
            review.setRating(i % 5 + 1); // Ratings from 1 to 5
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Review> result = reviewRepository.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertFalse(result.isLast());
    }

    @Test
    void findByPlace_Id_WithPageable_ShouldReturnPaginatedResults() {
        // Given
        Place place1 = new Place();
        place1.setPlaceName("Place 1");
        place1.setLatitude(40.7128);
        place1.setLongitude(-74.0060);
        place1.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace1 = placeRepository.save(place1);

        Place place2 = new Place();
        place2.setPlaceName("Place 2");
        place2.setLatitude(40.7129);
        place2.setLongitude(-74.0061);
        place2.setPlaceCategory(Category.COFFEE);
        Place savedPlace2 = placeRepository.save(place2);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        // Create 5 reviews for place1
        for (int i = 1; i <= 5; i++) {
            Review review = new Review();
            review.setPlace(savedPlace1);
            review.setUser(savedUser);
            review.setDescription("Review for Place 1 - " + i);
            review.setRating(i % 5 + 1);
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        // Create 3 reviews for place2
        for (int i = 1; i <= 3; i++) {
            Review review = new Review();
            review.setPlace(savedPlace2);
            review.setUser(savedUser);
            review.setDescription("Review for Place 2 - " + i);
            review.setRating(i % 5 + 1);
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Review> result = reviewRepository.findByPlace_Id(savedPlace1.getId(), pageable);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());
        assertTrue(result.getContent().stream().allMatch(r -> r.getPlace().getId().equals(savedPlace1.getId())));
    }

    @Test
    void findByUser_UserId_WithPageable_ShouldReturnPaginatedResults() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user1 = new User();
        user1.setUserName("user1");
        user1.setUserEmail("user1@example.com");
        user1.setPasswordHash("hashedpassword1");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUserName("user2");
        user2.setUserEmail("user2@example.com");
        user2.setPasswordHash("hashedpassword2");
        User savedUser2 = userRepository.save(user2);

        // Create 8 reviews for user1
        for (int i = 1; i <= 8; i++) {
            Review review = new Review();
            review.setPlace(savedPlace);
            review.setUser(savedUser1);
            review.setDescription("Review from User 1 - " + i);
            review.setRating(i % 5 + 1);
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        // Create 4 reviews for user2
        for (int i = 1; i <= 4; i++) {
            Review review = new Review();
            review.setPlace(savedPlace);
            review.setUser(savedUser2);
            review.setDescription("Review from User 2 - " + i);
            review.setRating(i % 5 + 1);
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        Pageable pageable = PageRequest.of(0, 5);

        // When
        Page<Review> result = reviewRepository.findByUser_UserId(savedUser1.getUserId(), pageable);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        assertEquals(8, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertFalse(result.isLast());
        assertTrue(result.getContent().stream().allMatch(r -> r.getUser().getUserId().equals(savedUser1.getUserId())));
    }

    @Test
    void findAllFromLast24Hours_ShouldReturnRecentReviews() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        Review recentReview = new Review();
        recentReview.setPlace(savedPlace);
        recentReview.setUser(savedUser);
        recentReview.setDescription("Recent review");
        recentReview.setRating(5);
        recentReview.setReviewDate(LocalDateTime.now());
        reviewRepository.save(recentReview);

        // When
        List<Review> result = reviewRepository.findAllFromLast24Hours();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals("Recent review")));
    }

    @Test
    void countReviewsByCategory_ShouldReturnCategoryCounts() {
        // Given
        Place restaurant = new Place();
        restaurant.setPlaceName("Restaurant");
        restaurant.setLatitude(40.7128);
        restaurant.setLongitude(-74.0060);
        restaurant.setPlaceCategory(Category.RESTAURANT);
        Place savedRestaurant = placeRepository.save(restaurant);

        Place cafe = new Place();
        cafe.setPlaceName("Cafe");
        cafe.setLatitude(40.7129);
        cafe.setLongitude(-74.0061);
        cafe.setPlaceCategory(Category.COFFEE);
        Place savedCafe = placeRepository.save(cafe);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        // Create 3 reviews for restaurant
        for (int i = 1; i <= 3; i++) {
            Review review = new Review();
            review.setPlace(savedRestaurant);
            review.setUser(savedUser);
            review.setDescription("Restaurant review " + i);
            review.setRating(i % 5 + 1);
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        // Create 2 reviews for cafe
        for (int i = 1; i <= 2; i++) {
            Review review = new Review();
            review.setPlace(savedCafe);
            review.setUser(savedUser);
            review.setDescription("Cafe review " + i);
            review.setRating(i % 5 + 1);
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        // When
        List<Object[]> result = reviewRepository.countReviewsByCategory();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        boolean hasRestaurant = false;
        boolean hasCafe = false;

        for (Object[] row : result) {
            Category category = (Category) row[0];
            Long count = (Long) row[1];

            if (category == Category.RESTAURANT) {
                hasRestaurant = true;
                assertEquals(3L, count);
            } else if (category == Category.COFFEE) {
                hasCafe = true;
                assertEquals(2L, count);
            }
        }

        assertTrue(hasRestaurant);
        assertTrue(hasCafe);
    }

    @Test
    void save_ShouldPersistReview() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        Review review = new Review();
        review.setPlace(savedPlace);
        review.setUser(savedUser);
        review.setDescription("Great place!");
        review.setRating(5);
        review.setReviewDate(LocalDateTime.now());

        // When
        Review savedReview = reviewRepository.save(review);

        // Then
        assertNotNull(savedReview.getId());
        assertEquals("Great place!", savedReview.getDescription());
        assertEquals(5, savedReview.getRating());
        assertEquals(savedPlace.getId(), savedReview.getPlace().getId());
        assertEquals(savedUser.getUserId(), savedReview.getUser().getUserId());
        assertNotNull(savedReview.getReviewDate());
    }

    @Test
    void findById_WhenReviewExists_ShouldReturnReview() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        Review review = new Review();
        review.setPlace(savedPlace);
        review.setUser(savedUser);
        review.setDescription("Great place!");
        review.setRating(5);
        review.setReviewDate(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);

        // When
        Optional<Review> result = reviewRepository.findById(savedReview.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(savedReview.getId(), result.get().getId());
        assertEquals("Great place!", result.get().getDescription());
    }

    @Test
    void findById_WhenReviewDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Review> result = reviewRepository.findById(999L);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_ShouldRemoveReview() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setPasswordHash("hashedpassword");
        User savedUser = userRepository.save(user);

        Review review = new Review();
        review.setPlace(savedPlace);
        review.setUser(savedUser);
        review.setDescription("To be deleted");
        review.setRating(3);
        review.setReviewDate(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);

        // When
        reviewRepository.deleteById(savedReview.getId());

        // Then
        Optional<Review> result = reviewRepository.findById(savedReview.getId());
        assertTrue(result.isEmpty());
    }
}