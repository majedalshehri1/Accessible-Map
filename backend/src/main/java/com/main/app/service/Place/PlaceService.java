package com.main.app.service.Place;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Action;
import com.main.app.Enum.Category;
import com.main.app.Enum.EntityType;
import com.main.app.Exceptions.BadRequestException;
import com.main.app.config.AuthUser;
import com.main.app.config.SecurityUtils;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.Place.DetailPlaceDto;
import com.main.app.dto.Place.MinimalPlaceDto;
import com.main.app.dto.Place.PlaceDto;
import com.main.app.dto.Place.PlaceWithStatsDTO;
import com.main.app.dto.Review.ReviewResponseDTO;
import com.main.app.model.Place.Place;
import com.main.app.model.Place.PlaceFeature;
import com.main.app.model.Place.PlaceImage;
import com.main.app.model.Review.Review;
import com.main.app.repository.Place.PlaceFeatureRepository;
import com.main.app.repository.Place.PlaceImageRepository;
import com.main.app.repository.Place.PlaceRepository;
import com.main.app.repository.Review.ReviewRepository;
import com.main.app.Exceptions.PlaceNotFoundException;
import com.main.app.Exceptions.DuplicatePlaceException;
import com.main.app.service.Admin.AdminLogService;
import com.main.app.service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceFeatureRepository placeFeatureRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlaceImageRepository placeImageRepository;

    @Autowired
    private AdminLogService adminLogService;
    @Autowired
    private UserService userService;


    public Place getPlaceOrThrow(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public DetailPlaceDto getPlaceDetails(Long placeId) {
        PlaceWithStatsDTO stats = placeRepository.findStatsByPlaceId(placeId);

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        // Features as strings
        List<AccessibillityType> features = place.getPlaceFeatures().stream()
                .filter(PlaceFeature::getIsAvaliable)
                .map(PlaceFeature::getAccessibillityType)
                .toList();


        List<String> images = place.getImages().stream()
                .map(PlaceImage::getImageUrl)
                .toList();

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
            r.setReviewDate(review.getReviewDate());
            return r;
        }).toList();

        DetailPlaceDto response = new DetailPlaceDto();
        response.setId(place.getId());
        response.setPlaceName(place.getPlaceName());
        response.setPlaceCategory(place.getPlaceCategory());
        response.setReviews(reviewDtos);
        response.setLatitude(place.getLatitude());
        response.setLongitude(place.getLongitude());
        response.setFeatures(features);
        response.setImages(images);
        response.setAvgRating(stats.getAvgRating());
        response.setReviewsCount(stats.getReviewCount());
        return response;
    }

    public PlaceDto convertToDto(Place place) {
        PlaceDto dto = new PlaceDto();
        dto.setId(place.getId());
        dto.setPlaceName(place.getPlaceName());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());
        dto.setCategory(place.getPlaceCategory());

        // Add list of images from place_images table
        List<String> imagesUrls = place.getImages()
                .stream().map(img -> img.getImageUrl()).collect(Collectors.toList());
        dto.setImageUrls(imagesUrls);

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


        Place saved = placeRepository.save(place);

        final Place finalSaved = saved;
        if(dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()){
            if(dto.getImageUrls().size() > 3){
                throw new IllegalArgumentException("Maximum 3 images per location.");
            }
            List<PlaceImage> images = dto.getImageUrls().stream()
                    .map(url -> {
                        PlaceImage img = new PlaceImage();
                        img.setImageUrl(url);
                        img.setPlace(finalSaved);
                        return img;
                    }).collect(Collectors.toList());

            placeImageRepository.saveAll(images);
            saved.setImages(images);
        }

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

        var me = SecurityUtils.currentUser();
        Long   actorId = me.map(AuthUser::id).orElse(null);
        String actorName =me.map(AuthUser::name).orElse(null) ;

        adminLogService.writeLog(
                EntityType.PLACE,
                Action.CREATE,
                saved.getId(),
                actorId,
                actorName,
                "تم اضافة مكان: " + "'"+saved.getPlaceName()+"'"
        );

        return convertToDto(saved);
    }


    public Place updatePlace(Place place){
        return placeRepository.save(place);
    }

    public List<Place> searchPlace(String name){
        if (name == null || name.isBlank())
            throw new BadRequestException("أدخل نص البحث.");

        return placeRepository.findByPlaceNameContainingIgnoreCase(name);
    }

    public void deletePlace(Long id){
        placeRepository.deleteById(id);
    }

    public PaginatedResponse<PlaceDto> getAllPlaces(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Place> placePage = placeRepository.findAll(pageable);

        List<PlaceDto> placeDtos = placePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                placeDtos,
                placePage.getNumber(),
                placePage.getSize(),
                placePage.getTotalElements(),
                placePage.getTotalPages(),
                placePage.isLast()
        );
    }

    public PaginatedResponse<PlaceDto> getPlaceCategory(Category category, int page, int size) {
        if (category == null)
            throw new BadRequestException("التصنيف مطلوب.");
        if (page < 0)
            throw new BadRequestException("page لا يمكن أن يكون سالب.");
        if (size < 1 || size > 100)
            throw new BadRequestException("size يجب أن يكون بين 1 و 100.");

        Pageable pageable = PageRequest.of(page, size);
        Page<Place> placePage = placeRepository.findByPlaceCategory(category, pageable);

        List<PlaceDto> placeDtos = placePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                placeDtos,
                placePage.getNumber(),
                placePage.getSize(),
                placePage.getTotalElements(),
                placePage.getTotalPages(),
                placePage.isLast()
        );
    }
    public List<Object[]> countPlacesByCategory(){
        return placeRepository.countPlacesByCategory();
    }

    public List<MinimalPlaceDto> getMinimalWithinBoundsPlaces
            (Double n, Double s, Double e, Double w, Double zoomLevel, String placeName, Category category) {
            // handle zoom case where lower than 11 so it will not try to get most places at once
            if (zoomLevel <= 11) {
                return Collections.emptyList();
            }

            List<Place> places;

            boolean doesPlaceNameExists = placeName != null && !placeName.isEmpty();
            boolean doesCategoryExists = category != null;

            if (doesPlaceNameExists && doesCategoryExists) {
                places = placeRepository.findWithinBoundsByNameAndCategory(n, s, e, w, placeName, category.name());
            } else if (doesPlaceNameExists) {
                places = placeRepository.findWithinBoundsByName(n, s, e, w, placeName);
            } else if (doesCategoryExists) {
                places = placeRepository.findWithinBoundsByCategory(n, s, e, w, category.name());
            } else {
                places = placeRepository.findWithinDistance(n, s, e, w);
            }
            return places.stream().map(this::convertToMinimalDto).collect(Collectors.toList());
        }

    private MinimalPlaceDto convertToMinimalDto(Place place) {
        MinimalPlaceDto dto = new MinimalPlaceDto();
        dto.setId(place.getId());
        dto.setPlaceName(place.getPlaceName());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());
        dto.setCategory(place.getPlaceCategory());
        return dto;
    }
    }


