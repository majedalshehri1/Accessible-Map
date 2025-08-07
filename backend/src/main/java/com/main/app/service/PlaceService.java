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

      public PlaceDto convertToDto(Place place) {
        PlaceDto dto = new PlaceDto();
        dto.setId(place.getId());
        dto.setPlaceName(place.getPlaceName());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());
        dto.setCategory(place.getPlaceCategory());
        dto.setImageUrl(place.getImageUrl());

        List<Review> reviews = reviewRepository.findByPlaceId(place.getId());

        List<ReviewResponseDTO> reviewDtos = reviews.stream().map(review -> {
            ReviewResponseDTO reviewDto = new ReviewResponseDTO();
            reviewDto.setId(review.getId());
            reviewDto.setPlaceName(place.getPlaceName());
            reviewDto.setPlaceCategory(place.getPlaceCategory());
            reviewDto.setUserName(review.getUser().getUserName());
            reviewDto.setDescription(review.getDescription());
            reviewDto.setRating(review.getRating());
            return reviewDto;
        }).collect(Collectors.toList());

        dto.setReviews(reviewDtos);

        return dto;
    }


    public Optional <Place> getPlaceById(Long id){

        return placeRepository.findById(id);
    }

    public PlaceDto createPlace(PlaceDto dto) {
        Place place = new Place();
        place.setPlaceName(dto.getPlaceName());
        place.setLatitude(dto.getLatitude());
        place.setLongitude(dto.getLongitude());
        place.setPlaceCategory(dto.getCategory());
        place.setImageUrl(dto.getImageUrl());

        // حفظ المكان
        Place saved = placeRepository.save(place);

        // إضافة الميزات
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

        // ✅ هنا يتم تحويل كائن الـ Place بعد الحفظ إلى DTO فيه ID
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
