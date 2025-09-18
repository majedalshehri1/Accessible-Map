package com.main.app.controller;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.config.AuthUser;
import com.main.app.config.SecurityUtils;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.Place.DetailPlaceDto;
import com.main.app.dto.Place.PlaceDto;
import com.main.app.model.Place.Place;
import com.main.app.service.Place.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static com.main.app.Enum.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceControllerTest {

    @Mock
    private PlaceService placeService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private PlaceController placeController;

    private Place place;
    private PlaceDto placeDto;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        place = new Place();
        place.setId(1L);
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);

        placeDto = new PlaceDto();
        placeDto.setId(1L);
        placeDto.setPlaceName("Test Place");
        placeDto.setLatitude(40.7128);
        placeDto.setLongitude(-74.0060);
        placeDto.setCategory(Category.RESTAURANT);

        authUser = new AuthUser(1L, "testuser", "test@example.com", USER);
    }

    @Test
    void getPlaceById_WhenPlaceExists_ShouldReturnPlaceDto() {
        when(placeService.getPlaceOrThrow(1L)).thenReturn(place);
        when(placeService.convertToDto(place)).thenReturn(placeDto);

        ResponseEntity<DetailPlaceDto> response = placeController.getPlaceById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(placeDto.getId(), response.getBody().getId());
        verify(placeService, times(1)).getPlaceOrThrow(1L);
        verify(placeService, times(1)).convertToDto(place);
    }

    @Test
    void createPlace_ShouldCreatePlaceSuccessfully() {
        when(placeService.createPlace(any(PlaceDto.class))).thenReturn(placeDto);

        ResponseEntity<PlaceDto> response = placeController.createPlace(placeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(placeDto.getId(), response.getBody().getId());
        verify(placeService, times(1)).createPlace(placeDto);
    }

    @Test
    void searchPlace_WithValidSearchTerm_ShouldReturnListOfPlaces() {
        List<Place> places = List.of(place);
        List<PlaceDto> placeDtos = List.of(placeDto);

        when(placeService.searchPlace("Test")).thenReturn(places);
        when(placeService.convertToDto(place)).thenReturn(placeDto);

        ResponseEntity<List<PlaceDto>> response = placeController.searchPlace("Test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(placeService, times(1)).searchPlace("Test");
    }

    @Test
    void deletePlace_ShouldDeletePlaceSuccessfully() {
        doNothing().when(placeService).deletePlace(1L);

        placeController.deletePlace(1L);

        verify(placeService, times(1)).deletePlace(1L);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        List<String> expectedCategories = Arrays.stream(Category.values())
                .map(Enum::name)
                .toList();

        ResponseEntity<List<String>> response = placeController.getAllCategory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedCategories.size(), response.getBody().size());
        assertTrue(response.getBody().containsAll(expectedCategories));
    }

    @Test
    void getAccessibilityType_ShouldReturnAllAccessibilityTypes() {
        List<String> expectedTypes = Arrays.stream(AccessibillityType.values())
                .map(Enum::name)
                .toList();

        ResponseEntity<List<String>> response = placeController.getAccessibillityType();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedTypes.size(), response.getBody().size());
        assertTrue(response.getBody().containsAll(expectedTypes));
    }

    @Test
    void getAllPlaces_ShouldReturnPaginatedResponse() {
        PaginatedResponse<PlaceDto> paginatedResponse = new PaginatedResponse<>(
                List.of(placeDto), 0, 10, 1L, 1, true
        );

        when(placeService.getAllPlaces(0, 10)).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<PlaceDto>> response = placeController.getAllPlaces(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        verify(placeService, times(1)).getAllPlaces(0, 10);
    }

    @Test
    void getAllPlaces_WithDefaultPagination_ShouldUseDefaults() {
        PaginatedResponse<PlaceDto> paginatedResponse = new PaginatedResponse<>(
                List.of(placeDto), 0, 10, 1L, 1, true
        );

        when(placeService.getAllPlaces(0, 10)).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<PlaceDto>> response = placeController.getAllPlaces(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(placeService, times(1)).getAllPlaces(0, 10);
    }

    @Test
    void getPlaceCategory_WithValidCategory_ShouldReturnPaginatedResponse() {
        PaginatedResponse<PlaceDto> paginatedResponse = new PaginatedResponse<>(
                List.of(placeDto), 0, 10, 1L, 1, true
        );

        when(placeService.getPlaceCategory(Category.RESTAURANT, 0, 10)).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<PlaceDto>> response = placeController.getPlaceCategory(Category.RESTAURANT, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        verify(placeService, times(1)).getPlaceCategory(Category.RESTAURANT, 0, 10);
    }

    @Test
    void getPlaceCategory_WithDefaultPagination_ShouldUseDefaults() {
        PaginatedResponse<PlaceDto> paginatedResponse = new PaginatedResponse<>(
                List.of(placeDto), 0, 10, 1L, 1, true
        );

        when(placeService.getPlaceCategory(Category.RESTAURANT, 0, 10)).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<PlaceDto>> response = placeController.getPlaceCategory(Category.RESTAURANT, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(placeService, times(1)).getPlaceCategory(Category.RESTAURANT, 0, 10);
    }
}