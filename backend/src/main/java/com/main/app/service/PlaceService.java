package com.main.app.service;

import com.main.app.Enum.Category;
import com.main.app.model.Place;
import com.main.app.repository.PlaceRepository;
import com.main.app.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> getAllPlaces(){
        return placeRepository.findAll();
    }

    public Optional <Place> getPlaceById(int id){
        return placeRepository.findById(id);
    }

    public Place createPlace(Place place){
        return placeRepository.save(place);
    }

    public Place updatePlace(Place place){
        return placeRepository.save(place);
    }

    public void deletePlace(int id){
        placeRepository.deleteById(id);
    }

    public List<Place> getPlaceCategory(Category category){
       return placeRepository.getCategory(category);

    }

}
