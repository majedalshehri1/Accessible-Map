package com.main.app.Repository.Place;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.model.Place.Place;
import com.main.app.model.Place.PlaceFeature;
import com.main.app.repository.Place.PlaceFeatureRepository;
import com.main.app.repository.Place.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PlaceFeatureRepositoryTest {

    @Autowired
    private PlaceFeatureRepository placeFeatureRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    void findByPlace_WhenPlaceHasFeatures_ShouldReturnFeatures() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude("40.7128");
        place.setLongitude("-74.0060");
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        PlaceFeature feature1 = new PlaceFeature();
        feature1.setPlace(savedPlace);
        feature1.setAccessibillityType(AccessibillityType.PARKING);
        feature1.setIsAvaliable(true);

        PlaceFeature feature2 = new PlaceFeature();
        feature2.setPlace(savedPlace);
        feature2.setAccessibillityType(AccessibillityType.ELEVATORS);
        feature2.setIsAvaliable(true);

        placeFeatureRepository.saveAll(List.of(feature1, feature2));

        // When
        List<PlaceFeature> result = placeFeatureRepository.findByPlace(savedPlace);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(f -> f.getAccessibillityType() == AccessibillityType.PARKING));
        assertTrue(result.stream().anyMatch(f -> f.getAccessibillityType() == AccessibillityType.ELEVATORS));
    }

    @Test
    void findByPlace_WhenPlaceHasNoFeatures_ShouldReturnEmptyList() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude("40.7128");
        place.setLongitude("-74.0060");
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        // When
        List<PlaceFeature> result = placeFeatureRepository.findByPlace(savedPlace);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_ShouldPersistPlaceFeature() {
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude("40.7128");
        place.setLongitude("-74.0060");
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        PlaceFeature feature = new PlaceFeature();
        feature.setPlace(savedPlace);
        feature.setAccessibillityType(AccessibillityType.ELEVATORS);
        feature.setIsAvaliable(true);

        PlaceFeature savedFeature = placeFeatureRepository.save(feature);

        assertNotNull(savedFeature.getId());
        assertEquals(AccessibillityType.ELEVATORS, savedFeature.getAccessibillityType());
        assertEquals(savedPlace.getId(), savedFeature.getPlace().getId());
    }

    @Test
    void findById_WhenFeatureExists_ShouldReturnFeature() {
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude("40.7128");
        place.setLongitude("-74.0060");
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        PlaceFeature feature = new PlaceFeature();
        feature.setPlace(savedPlace);
        feature.setAccessibillityType(AccessibillityType.ELEVATORS);
        feature.setIsAvaliable(true);
        PlaceFeature savedFeature = placeFeatureRepository.save(feature);

        var result = placeFeatureRepository.findById(savedFeature.getId());

        assertTrue(result.isPresent());
        assertEquals(savedFeature.getId(), result.get().getId());
        assertEquals(AccessibillityType.ELEVATORS, result.get().getAccessibillityType());
    }
}