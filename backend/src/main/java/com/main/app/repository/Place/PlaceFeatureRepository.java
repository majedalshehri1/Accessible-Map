package com.main.app.repository.Place;

import com.main.app.model.Place.Place;
import com.main.app.model.Place.PlaceFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceFeatureRepository extends JpaRepository<PlaceFeature, Long> {
    List<PlaceFeature> findByPlace(Place place);



//    void deleteByPlace(Place place);
//
}