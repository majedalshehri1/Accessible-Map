package com.main.app.service.Place;

import com.main.app.model.Place.PlaceFeature;
import com.main.app.repository.Place.PlaceFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceFeatureService {

    @Autowired
    private PlaceFeatureRepository placeFeatureRepository;

    public List<PlaceFeature> getPlaceFeatures(){
        return placeFeatureRepository.findAll();
    }

    public Optional<PlaceFeature> getPlaceFeatureById(Long id){
        return placeFeatureRepository.findById(id);
    }

    public PlaceFeature savePlaceFeature(PlaceFeature placeFeature){
        return placeFeatureRepository.save(placeFeature);
    }

    public void deletePlaceFeatureById(Long id){
        placeFeatureRepository.deleteById(id);
    }



}
