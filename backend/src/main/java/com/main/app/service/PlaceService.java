package com.main.app.service;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.dto.PlaceDto;
import com.main.app.model.Place;
import com.main.app.model.PlaceFeature;
import com.main.app.repository.PlaceFeatureRepository;
import com.main.app.repository.PlaceRepository;
import com.main.app.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceFeatureRepository placeFeatureRepository;

    public List<Place> getAllPlaces(){
        return placeRepository.findAll();
    }

    public Optional <Place> getPlaceById(Long id){

        return placeRepository.findById(id);
    }

    public Place createPlace(PlaceDto dto){
        Place place = new Place();
        place.setPlaceName(dto.getPlaceName());
        place.setLatitude(dto.getLatitude());
        place.setLongitude(dto.getLongitude());
        place.setPlaceCategory(dto.getCategory());
        place.setImageUrl(dto.getImageUrl());

        Place saved = placeRepository.save(place);

        if(dto.getAccessibilityFeatures() != null){
            List<PlaceFeature> features = new ArrayList<>();
            for (String type : dto.getAccessibilityFeatures()) {
                PlaceFeature feature = new PlaceFeature();
                feature.setAccessibillityType(AccessibillityType.getByName(type));
                feature.setIsAvaliable(true);
                feature.setPlace(saved);
                features.add(feature);
            }
            placeFeatureRepository.saveAll(features);
            saved.setPlaceFeatures(features);
            saved = placeRepository.save(saved);
        }

        return saved;
    }

    public Place updatePlace(Place place){
        return placeRepository.save(place);
    }

    public List<Place> searchPlace( String name){
        return placeRepository.findByPlaceNameContainingIgnoreCase(name);
    }

    public void deletePlace(Long id){
        placeRepository.deleteById(id);
    }

    public List<Place> getPlaceCategory(Category category){
       return placeRepository.findByPlaceCategory(category);

    }



}
