package com.main.app.Repository.Place;

import com.main.app.Enum.Category;
import com.main.app.model.Place.Place;
import com.main.app.repository.Place.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    void findByPlaceCategory_WhenCategoryExists_ShouldReturnPlaces() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Restaurant");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        placeRepository.save(place);

        // When
        List<Place> result = placeRepository.findByPlaceCategory(Category.RESTAURANT);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(Category.RESTAURANT, result.get(0).getPlaceCategory());
    }

    @Test
    void findByPlaceCategory_WhenCategoryDoesNotExist_ShouldReturnEmptyList() {
        // When
        List<Place> result = placeRepository.findByPlaceCategory(Category.HOSPITAL);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByPlaceNameContainingIgnoreCase_WhenNameExists_ShouldReturnPlaces() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Restaurant");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        placeRepository.save(place);

        // When
        List<Place> result = placeRepository.findByPlaceNameContainingIgnoreCase("rest");

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getPlaceName().toLowerCase().contains("rest"));
    }

    @Test
    void findByPlaceNameContainingIgnoreCase_WhenNameDoesNotExist_ShouldReturnEmptyList() {
        // When
        List<Place> result = placeRepository.findByPlaceNameContainingIgnoreCase("nonexistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByPlaceNameIgnoreCase_WhenNameExists_ShouldReturnTrue() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Restaurant");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        placeRepository.save(place);

        // When
        boolean exists = placeRepository.existsByPlaceNameIgnoreCase("test restaurant");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByPlaceNameIgnoreCase_WhenNameDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = placeRepository.existsByPlaceNameIgnoreCase("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void countPlacesByCategory_ShouldReturnCategoryCounts() {
        // Given
        Place place1 = new Place();
        place1.setPlaceName("Restaurant 1");
        place1.setLatitude(40.7128);
        place1.setLongitude(-74.0060);
        place1.setPlaceCategory(Category.RESTAURANT);

        Place place2 = new Place();
        place2.setPlaceName("Restaurant 2");
        place2.setLatitude(40.7129);
        place2.setLongitude(-74.0061);
        place2.setPlaceCategory(Category.RESTAURANT);

        Place place3 = new Place();
        place3.setPlaceName("Cafe");
        place3.setLatitude(40.7130);
        place3.setLongitude(-74.0062);
        place3.setPlaceCategory(Category.COFFEE);

        placeRepository.saveAll(List.of(place1, place2, place3));

        // When
        List<Object[]> result = placeRepository.countPlacesByCategory();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify that we have counts for each category
        boolean hasRestaurant = false;
        boolean hasCafe = false;

        for (Object[] row : result) {
            Category category = (Category) row[0];
            Long count = (Long) row[1];

            if (category == Category.RESTAURANT) {
                hasRestaurant = true;
                assertEquals(2L, count);
            } else if (category == Category.COFFEE) {
                hasCafe = true;
                assertEquals(1L, count);
            }
        }

        assertTrue(hasRestaurant);
        assertTrue(hasCafe);
    }

    @Test
    void findAll_WithPageable_ShouldReturnPaginatedResults() {
        // Given
        for (int i = 1; i <= 15; i++) {
            Place place = new Place();
            place.setPlaceName("Place " + i);
            place.setLatitude(40.712 + i);
            place.setLongitude(-74.006 + i);
            place.setPlaceCategory(Category.RESTAURANT);
            placeRepository.save(place);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Place> result = placeRepository.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertFalse(result.isLast());
    }

    @Test
    void findByPlaceCategory_WithPageable_ShouldReturnPaginatedResults() {
        // Given
        for (int i = 1; i <= 15; i++) {
            Place place = new Place();
            place.setPlaceName("Restaurant " + i);
            place.setLatitude(40.712 + i);
            place.setLongitude(-74.006 + i);
            place.setPlaceCategory(Category.RESTAURANT);
            placeRepository.save(place);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Place> result = placeRepository.findByPlaceCategory(Category.RESTAURANT, pageable);

        // Then
        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(Category.RESTAURANT, result.getContent().get(0).getPlaceCategory());
    }

    @Test
    void findById_WhenPlaceExists_ShouldReturnPlace() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        // When
        Optional<Place> result = placeRepository.findById(savedPlace.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(savedPlace.getId(), result.get().getId());
        assertEquals("Test Place", result.get().getPlaceName());
    }

    @Test
    void findById_WhenPlaceDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Place> result = placeRepository.findById(999L);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void save_ShouldPersistPlace() {
        // Given
        Place place = new Place();
        place.setPlaceName("New Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);

        // When
        Place savedPlace = placeRepository.save(place);

        // Then
        assertNotNull(savedPlace.getId());
        assertEquals("New Place", savedPlace.getPlaceName());
        assertEquals(Category.RESTAURANT, savedPlace.getPlaceCategory());
    }

    @Test
    void deleteById_ShouldRemovePlace() {
        // Given
        Place place = new Place();
        place.setPlaceName("To be deleted");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        // When
        placeRepository.deleteById(savedPlace.getId());

        // Then
        Optional<Place> result = placeRepository.findById(savedPlace.getId());
        assertTrue(result.isEmpty());
    }
}