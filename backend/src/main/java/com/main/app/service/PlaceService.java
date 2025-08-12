package com.main.app.service;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.dto.PlaceDto;
import com.main.app.dto.ReviewResponseDTO;
import com.main.app.model.Place;
import com.main.app.model.PlaceFeature;
import com.main.app.model.Review;
import com.main.app.repository.PlaceFeatureRepository;
import com.main.app.repository.PlaceRepository;
import com.main.app.repository.ReviewRepository;
import com.main.app.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import com.main.app.Exceptions.PlaceNotFoundException;
import com.main.app.Exceptions.DuplicatePlaceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceFeatureRepository placeFeatureRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    public List<Place> getAllPlaces(){
        return placeRepository.findAll();
    }

    public Place getPlaceOrThrow(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotFoundException(id));
    }

    public PlaceDto convertToDto(Place place) {
        PlaceDto dto = new PlaceDto();
        dto.setId(place.getId());
        dto.setPlaceName(place.getPlaceName());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());
        dto.setCategory(place.getPlaceCategory());
        dto.setImageUrl(place.getImageUrl());

        List<String> features = placeFeatureRepository.findByPlace(place).stream()
                .map(PlaceFeature::getAccessibillityType)
                .map(Enum::name)
                .collect(Collectors.toList());
        dto.setAccessibilityFeatures(features);

        // Reviews
        List<Review> reviews = reviewRepository.findByPlaceId(place.getId());
        List<ReviewResponseDTO> reviewDtos = reviews.stream().map(review -> {
            ReviewResponseDTO r = new ReviewResponseDTO();
            r.setId(review.getId());
            r.setPlaceName(place.getPlaceName());
            r.setPlaceCategory(place.getPlaceCategory());
            r.setUserName(review.getUser().getUserName());
            r.setDescription(review.getDescription());
            r.setRating(review.getRating());
            return r;
        }).collect(Collectors.toList());
        dto.setReviews(reviewDtos);

        return dto;
    }


    public Optional <Place> getPlaceById(Long id){

        return placeRepository.findById(id);
    }

    public PlaceDto createPlace(PlaceDto dto) {

        if (placeRepository.existsByPlaceNameIgnoreCase(dto.getPlaceName())) {
            throw new DuplicatePlaceException(dto.getPlaceName());
        }

        Place place = new Place();
        place.setPlaceName(dto.getPlaceName());
        place.setLatitude(dto.getLatitude());
        place.setLongitude(dto.getLongitude());
        place.setPlaceCategory(dto.getCategory());
        place.setImageUrl(dto.getImageUrl());

        Place saved = placeRepository.save(place);

        if (dto.getAccessibilityFeatures() != null) {
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

        return convertToDto(saved);
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
