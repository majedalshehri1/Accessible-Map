package com.main.app.service.Review;

import com.main.app.Enum.Category;
import com.main.app.dto.Review.ReviewRequestDTO;
import com.main.app.dto.Review.ReviewResponseDTO;
import com.main.app.model.Place.Place;
import com.main.app.model.Review.Review;
import com.main.app.model.User.User;
import com.main.app.repository.Place.PlaceRepository;
import com.main.app.repository.Review.ReviewRepository;
import com.main.app.repository.User.UserRepository;
import com.main.app.service.Admin.AdminLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminLogService adminLogService;

    @Test
    void createReview_ShouldReturnSavedReview() {


        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setUserId(1L);
        request.setPlaceId(10L);
        request.setDescription("Great place!");
        request.setRating(5);

        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUserName("Majed");

        Place mockPlace = new Place();
        mockPlace.setId(10L);
        mockPlace.setPlaceName("Test Place");
        mockPlace.setPlaceCategory(Category.valueOf("RESTAURANT"));

        Review mockReview = new Review();
        mockReview.setId(100L);
        mockReview.setUser(mockUser);
        mockReview.setPlace(mockPlace);
        mockReview.setDescription("Great place!");
        mockReview.setRating(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(placeRepository.findById(10L)).thenReturn(Optional.of(mockPlace));
        when(reviewRepository.save(any(Review.class))).thenReturn(mockReview);

        // Act
        ReviewResponseDTO result = reviewService.createReview(request);

        // Assert
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getDescription()).isEqualTo("Great place!");
        assertThat(result.getUserName()).isEqualTo("Majed");
        assertThat(result.getPlaceName()).isEqualTo("Test Place");

        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(adminLogService, times(1)).writeLog(
                any(), any(), any(), any(), any(), any()
        );
    }
}
