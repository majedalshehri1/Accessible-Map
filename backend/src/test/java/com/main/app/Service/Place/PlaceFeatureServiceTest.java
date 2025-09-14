package com.main.app.Service.Place;

import com.main.app.model.Place.PlaceFeature;
import com.main.app.repository.Place.PlaceFeatureRepository;
import com.main.app.service.Place.PlaceFeatureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceFeatureServiceTest {

    @Mock
    private PlaceFeatureRepository placeFeatureRepository;

    @InjectMocks
    private PlaceFeatureService placeFeatureService;

    private PlaceFeature placeFeature;

    @BeforeEach
    void setUp() {
        placeFeature = new PlaceFeature();
        placeFeature.setId(1L);
    }

    @Test
    void getPlaceFeatures_ShouldReturnAllFeatures() {
        when(placeFeatureRepository.findAll()).thenReturn(List.of(placeFeature));

        List<PlaceFeature> result = placeFeatureService.getPlaceFeatures();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(placeFeature.getId(), result.get(0).getId());
    }

    @Test
    void getPlaceFeatureById_WhenFeatureExists_ShouldReturnFeature() {
        when(placeFeatureRepository.findById(1L)).thenReturn(Optional.of(placeFeature));

        Optional<PlaceFeature> result = placeFeatureService.getPlaceFeatureById(1L);

        assertTrue(result.isPresent());
        assertEquals(placeFeature.getId(), result.get().getId());
    }

    @Test
    void getPlaceFeatureById_WhenFeatureNotFound_ShouldReturnEmpty() {
        when(placeFeatureRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<PlaceFeature> result = placeFeatureService.getPlaceFeatureById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void savePlaceFeature_ShouldSaveFeatureSuccessfully() {
        when(placeFeatureRepository.save(placeFeature)).thenReturn(placeFeature);

        PlaceFeature result = placeFeatureService.savePlaceFeature(placeFeature);

        assertNotNull(result);
        assertEquals(placeFeature.getId(), result.getId());
        verify(placeFeatureRepository, times(1)).save(placeFeature);
    }

    @Test
    void deletePlaceFeatureById_ShouldDeleteFeatureSuccessfully() {
        doNothing().when(placeFeatureRepository).deleteById(1L);

        assertDoesNotThrow(() -> placeFeatureService.deletePlaceFeatureById(1L));
        verify(placeFeatureRepository, times(1)).deleteById(1L);
    }
}