package com.main.app.service;

import com.main.app.Enum.*;
import com.main.app.Exceptions.BadRequestException;
import com.main.app.Exceptions.DuplicatePlaceException;
import com.main.app.Exceptions.PlaceNotFoundException;
import com.main.app.config.AuthUser;
import com.main.app.config.SecurityUtils;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.Place.PlaceDto;
import com.main.app.model.Place.Place;
import com.main.app.model.Place.PlaceFeature;
import com.main.app.model.Place.PlaceImage;
import com.main.app.model.Review.Review;
import com.main.app.model.User.User;
import com.main.app.repository.Place.PlaceFeatureRepository;
import com.main.app.repository.Place.PlaceImageRepository;
import com.main.app.repository.Place.PlaceRepository;
import com.main.app.repository.Review.ReviewRepository;
import com.main.app.service.Admin.AdminLogService;
import com.main.app.service.Place.PlaceService;
import com.main.app.service.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static com.main.app.Enum.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlaceFeatureRepository placeFeatureRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PlaceImageRepository placeImageRepository;

    @Mock
    private AdminLogService adminLogService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PlaceService placeService;

    private Place place;
    private PlaceDto placeDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L); // Changed from setId to setUserId
        user.setUserName("testuser");

        place = new Place();
        place.setId(1L);
        place.setPlaceName("Test Place");
        place.setLatitude("40.7128");
        place.setLongitude("-74.0060");
        place.setPlaceCategory(Category.RESTAURANT);

        PlaceImage image = new PlaceImage();
        image.setImageUrl("http://example.com/image.jpg");
        image.setPlace(place);
        place.setImages(List.of(image));

        PlaceFeature feature = new PlaceFeature();
        feature.setAccessibillityType(AccessibillityType.ELEVATORS);
        feature.setIsAvaliable(true);
        feature.setPlace(place);
        place.setPlaceFeatures(List.of(feature));

        placeDto = new PlaceDto();
        placeDto.setPlaceName("Test Place");
        placeDto.setLatitude("40.7128");
        placeDto.setLongitude("-74.0060");
        placeDto.setCategory(Category.RESTAURANT);
        placeDto.setImageUrls(List.of("http://example.com/image.jpg"));
        placeDto.setAccessibilityFeatures(List.of("ELEVATORS"));
    }

    @Test
    void getPlaceOrThrow_WhenPlaceExists_ShouldReturnPlace() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

        Place result = placeService.getPlaceOrThrow(1L);

        assertNotNull(result);
        assertEquals(place.getId(), result.getId());
        assertEquals(place.getPlaceName(), result.getPlaceName());
    }

    @Test
    void getPlaceOrThrow_WhenPlaceNotFound_ShouldThrowException() {
        when(placeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PlaceNotFoundException.class, () -> placeService.getPlaceOrThrow(1L));
    }

    @Test
    void convertToDto_ShouldConvertPlaceToDtoCorrectly() {
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setDescription("Great place!");
        review.setRating(5);
        review.setPlace(place);

        when(reviewRepository.findByPlaceId(1L)).thenReturn(List.of(review));
        when(placeFeatureRepository.findByPlace(place)).thenReturn(place.getPlaceFeatures());

        PlaceDto result = placeService.convertToDto(place);

        assertNotNull(result);
        assertEquals(place.getId(), result.getId());
        assertEquals(place.getPlaceName(), result.getPlaceName());
        assertEquals(place.getLatitude(), result.getLatitude());
        assertEquals(place.getLongitude(), result.getLongitude());
        assertEquals(place.getPlaceCategory(), result.getCategory());
        assertNotNull(result.getImageUrls());
        assertNotNull(result.getAccessibilityFeatures());
        assertNotNull(result.getReviews());
    }

    @Test
    void getPlaceById_WhenPlaceExists_ShouldReturnPlace() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

        Optional<Place> result = placeService.getPlaceById(1L);

        assertTrue(result.isPresent());
        assertEquals(place.getId(), result.get().getId());
    }

    @Test
    void getPlaceById_WhenPlaceNotFound_ShouldReturnEmpty() {
        when(placeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Place> result = placeService.getPlaceById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void createPlace_ShouldCreatePlaceSuccessfully() {
        when(placeRepository.existsByPlaceNameIgnoreCase("Test Place")).thenReturn(false);
        when(placeRepository.save(any(Place.class))).thenReturn(place);
        when(placeImageRepository.saveAll(anyList())).thenReturn(place.getImages());
        when(placeFeatureRepository.saveAll(anyList())).thenReturn(place.getPlaceFeatures());

        // Mock security context
        AuthUser authUser = new AuthUser(1L, "testuser", "test@example.com", USER);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityUtils.currentUser()).thenReturn(Optional.of(authUser));

        PlaceDto result = placeService.createPlace(placeDto);

        assertNotNull(result);
        assertEquals(placeDto.getPlaceName(), result.getPlaceName());
//        verify(placeRepository, times(1)).save(any(Place.class));
        verify(placeRepository, times(2)).save(any(Place.class));
        verify(placeImageRepository, times(1)).saveAll(anyList());
        verify(placeFeatureRepository, times(1)).saveAll(anyList());
        verify(adminLogService, times(1)).writeLog(any(), any(), any(), any(), any(), any());
    }

    @Test
    void createPlace_WithDuplicateName_ShouldThrowException() {
        when(placeRepository.existsByPlaceNameIgnoreCase("Test Place")).thenReturn(true);

        assertThrows(DuplicatePlaceException.class, () -> placeService.createPlace(placeDto));
    }

    @Test
    void createPlace_WithTooManyImages_ShouldThrowException() {
        placeDto.setImageUrls(List.of("url1", "url2", "url3", "url4")); // More than 3

        when(placeRepository.existsByPlaceNameIgnoreCase("Test Place")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> placeService.createPlace(placeDto));
    }

    @Test
    void updatePlace_ShouldUpdatePlaceSuccessfully() {
        when(placeRepository.save(place)).thenReturn(place);

        Place result = placeService.updatePlace(place);

        assertNotNull(result);
        assertEquals(place.getId(), result.getId());
        verify(placeRepository, times(1)).save(place);
    }

    @Test
    void searchPlace_WithValidName_ShouldReturnPlaces() {
        when(placeRepository.findByPlaceNameContainingIgnoreCase("Test")).thenReturn(List.of(place));

        List<Place> result = placeService.searchPlace("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(place.getPlaceName(), result.get(0).getPlaceName());
    }

    @Test
    void searchPlace_WithNullName_ShouldThrowException() {
        assertThrows(BadRequestException.class, () -> placeService.searchPlace(null));
    }

    @Test
    void searchPlace_WithBlankName_ShouldThrowException() {
        assertThrows(BadRequestException.class, () -> placeService.searchPlace("   "));
    }

    @Test
    void deletePlace_ShouldDeletePlaceSuccessfully() {
        doNothing().when(placeRepository).deleteById(1L);

        assertDoesNotThrow(() -> placeService.deletePlace(1L));
        verify(placeRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllPlaces_ShouldReturnPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Place> placePage = new PageImpl<>(List.of(place), pageable, 1);

        when(placeRepository.findAll(pageable)).thenReturn(placePage);
        when(reviewRepository.findByPlaceId(1L)).thenReturn(List.of());
        when(placeFeatureRepository.findByPlace(place)).thenReturn(place.getPlaceFeatures());

        PaginatedResponse<PlaceDto> result = placeService.getAllPlaces(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());
    }

    @Test
    void getPlaceCategory_WithValidCategory_ShouldReturnPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Place> placePage = new PageImpl<>(List.of(place), pageable, 1);

        when(placeRepository.findByPlaceCategory(Category.RESTAURANT, pageable)).thenReturn(placePage);
        when(reviewRepository.findByPlaceId(1L)).thenReturn(List.of());
        when(placeFeatureRepository.findByPlace(place)).thenReturn(place.getPlaceFeatures());

        PaginatedResponse<PlaceDto> result = placeService.getPlaceCategory(Category.RESTAURANT, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(Category.RESTAURANT, result.getContent().get(0).getCategory());
    }

    @Test
    void getPlaceCategory_WithNullCategory_ShouldThrowException() {
        assertThrows(BadRequestException.class, () -> placeService.getPlaceCategory(null, 0, 10));
    }

    @Test
    void getPlaceCategory_WithNegativePage_ShouldThrowException() {
        assertThrows(BadRequestException.class, () -> placeService.getPlaceCategory(Category.RESTAURANT, -1, 10));
    }

    @Test
    void getPlaceCategory_WithInvalidSize_ShouldThrowException() {
        assertThrows(BadRequestException.class, () -> placeService.getPlaceCategory(Category.RESTAURANT, 0, 0));
        assertThrows(BadRequestException.class, () -> placeService.getPlaceCategory(Category.RESTAURANT, 0, 101));
    }

    @Test
    void countPlacesByCategory_ShouldReturnCounts() {
        Object[] countData = {Category.RESTAURANT, 5L};
        List<Object[]> countList = Collections.singletonList(countData);


        when(placeRepository.countPlacesByCategory()).thenReturn(countList);

        List<Object[]> result = placeService.countPlacesByCategory();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Category.RESTAURANT, result.get(0)[0]);
        assertEquals(5L, result.get(0)[1]);
    }
}