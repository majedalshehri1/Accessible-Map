package com.main.app.repository;

import com.main.app.model.PlaceFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceFeatureRepository extends JpaRepository<PlaceFeature,Integer> {
}
