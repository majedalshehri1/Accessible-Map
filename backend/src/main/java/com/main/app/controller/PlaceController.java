package com.main.app.controller;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.dto.PlaceDto;
import com.main.app.model.Place;
import com.main.app.model.PlaceFeature;
import com.main.app.repository.PlaceFeatureRepository;
import com.main.app.repository.PlaceRepository;
import com.main.app.service.PlaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
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
    @Autowired
    private PlaceRepository placeRepository;

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

//        List<AccessibillityType> features = placeFeatureRepository.findByPlace(place).stream()
//                .map(PlaceFeature::getAccessibillityType).collect(Collectors.toList());
        List<String> features = placeFeatureRepository.findByPlace(place).stream()
                .map(PlaceFeature::getAccessibillityType)
                .map(AccessibillityType::toString)
                .collect(Collectors.toList());
        placeDto.setAccessibilityFeatures(features);
        return  placeDto;
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaceDto>> searchPlace(@RequestParam String search) {
        List<Place> places = placeService.searchPlace(search);
        List<PlaceDto> dtos = places.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
    @DeleteMapping("/{id}")
    public void deletePlace(@PathVariable Long id){
        placeService.deletePlace(id);
    }

    @GetMapping("/categories")
        public ResponseEntity<List<String>> getAllCategory(){
        List <String> categories = Arrays.stream(Category.values()).
                map(Enum::name).toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/Accessibility")
    public ResponseEntity<List<String>> getAccessibillityType(){
        List<String> accessibilityType = Arrays.stream(AccessibillityType.values()).
                map(Enum::name).toList();
        return ResponseEntity.ok(accessibilityType);
    }

    @GetMapping("category")
    public ResponseEntity<List<PlaceDto>> getPlaceCategory(@RequestParam Category category){
        List<Place> places = placeRepository.findByPlaceCategory(category);
        List<PlaceDto> dtos = places.stream().map(this::convertToDto).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);


    }



}


