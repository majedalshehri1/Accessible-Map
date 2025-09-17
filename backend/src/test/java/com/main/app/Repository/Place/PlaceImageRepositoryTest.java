package com.main.app.Repository.Place;

import com.main.app.Enum.Category;
import com.main.app.model.Place.Place;
import com.main.app.model.Place.PlaceImage;
import com.main.app.repository.Place.PlaceImageRepository;
import com.main.app.repository.Place.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PlaceImageRepositoryTest {

    @Autowired
    private PlaceImageRepository placeImageRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    void save_ShouldPersistPlaceImage() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        PlaceImage image = new PlaceImage();
        image.setPlace(savedPlace);
        image.setImageUrl("http://example.com/image.jpg");

        // When
        PlaceImage savedImage = placeImageRepository.save(image);

        // Then
        assertNotNull(savedImage.getId());
        assertEquals("http://example.com/image.jpg", savedImage.getImageUrl());
        assertEquals(savedPlace.getId(), savedImage.getPlace().getId());
    }

    @Test
    void findById_WhenImageExists_ShouldReturnImage() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        PlaceImage image = new PlaceImage();
        image.setPlace(savedPlace);
        image.setImageUrl("http://example.com/image.jpg");
        PlaceImage savedImage = placeImageRepository.save(image);

        // When
        Optional<PlaceImage> result = placeImageRepository.findById(savedImage.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(savedImage.getId(), result.get().getId());
        assertEquals("http://example.com/image.jpg", result.get().getImageUrl());
    }

    @Test
    void findAll_ShouldReturnAllImages() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        PlaceImage image1 = new PlaceImage();
        image1.setPlace(savedPlace);
        image1.setImageUrl("http://example.com/image1.jpg");

        PlaceImage image2 = new PlaceImage();
        image2.setPlace(savedPlace);
        image2.setImageUrl("http://example.com/image2.jpg");

        placeImageRepository.saveAll(List.of(image1, image2));

        // When
        List<PlaceImage> result = placeImageRepository.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 2);
        assertTrue(result.stream().anyMatch(img -> img.getImageUrl().equals("http://example.com/image1.jpg")));
        assertTrue(result.stream().anyMatch(img -> img.getImageUrl().equals("http://example.com/image2.jpg")));
    }

    @Test
    void deleteById_ShouldRemoveImage() {
        // Given
        Place place = new Place();
        place.setPlaceName("Test Place");
        place.setLatitude(40.7128);
        place.setLongitude(-74.0060);
        place.setPlaceCategory(Category.RESTAURANT);
        Place savedPlace = placeRepository.save(place);

        PlaceImage image = new PlaceImage();
        image.setPlace(savedPlace);
        image.setImageUrl("http://example.com/image.jpg");
        PlaceImage savedImage = placeImageRepository.save(image);

        // When
        placeImageRepository.deleteById(savedImage.getId());

        // Then
        Optional<PlaceImage> result = placeImageRepository.findById(savedImage.getId());
        assertTrue(result.isEmpty());
    }
}