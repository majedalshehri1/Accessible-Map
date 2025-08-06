package com.main.app.controller;

import com.main.app.Enum.AccessibillityType;
import com.main.app.dto.PlaceDto;
import com.main.app.model.Place;
import com.main.app.model.PlaceFeature;
import com.main.app.repository.PlaceFeatureRepository;
import com.main.app.service.PlaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private PlaceService placeService;
    @Autowired
    private PlaceFeatureRepository placeFeatureRepository;

    @GetMapping("/all")
    public ResponseEntity <List<Place>> getAllPlaces(){
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("{id}")
    public ResponseEntity<Optional<Place>> getPlaceById(@PathVariable Long id){
        return ResponseEntity.ok(placeService.getPlaceById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<PlaceDto> createPlace(@Valid @RequestBody PlaceDto dto) {
        Place saved = placeService.createPlace(dto);
        return ResponseEntity.ok(convertToDto(saved));
    }
    private PlaceDto convertToDto(Place place){
        PlaceDto placeDto = new PlaceDto();
        placeDto.setPlaceName(place.getPlaceName());
        placeDto.setLongitude(place.getLongitude());
        placeDto.setLatitude(place.getLatitude());
        placeDto.setCategory(place.getPlaceCategory());
        placeDto.setImageUrl(place.getImageUrl());

        List<AccessibillityType> features = placeFeatureRepository.findByPlace(place).stream().map(PlaceFeature::getAccessibillityType).collect(Collectors.toList());
        placeDto.setAccessibilityFeatures(features);
        return  placeDto;
    }
    //@GetMapping("/{name}")
    //public ResponseEntity<List<Place>> getPlacesByName(@PathVariable String name){
//return ResponseEntity.ok(placeService.findByPlaceName(name));
    //}


    @DeleteMapping("/{id}")
    public void deletePlace(@PathVariable Long id){
        placeService.deletePlace(id);
    }



}
