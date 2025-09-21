package com.main.app.controller;

import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.Place.DetailPlaceDto;
import com.main.app.dto.Place.MinimalPlaceDto;
import com.main.app.dto.Place.PlaceDto;
import com.main.app.model.Place.Place;
import com.main.app.service.Place.PlaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @GetMapping("{id}")
    public ResponseEntity<DetailPlaceDto>  getPlaceById(@PathVariable Long id){
        DetailPlaceDto place = placeService.getPlaceDetails(id);
        return ResponseEntity.ok(place);
    }

    @PostMapping("/create")
    public ResponseEntity<PlaceDto> createPlace(@Valid @RequestBody PlaceDto dto) {
        PlaceDto created = placeService.createPlace(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaceDto>> searchPlace(@RequestParam String search) {
        List<Place> places = placeService.searchPlace(search);

        List<PlaceDto> dtos = places.stream()
                .map(placeService::convertToDto)
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

    @GetMapping("/all")
    public ResponseEntity<PaginatedResponse<PlaceDto>> getAllPlaces(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(placeService.getAllPlaces(page, size));
    }

    // this GET request is highly unlikely to hit an HTTP 414 error
    // the URL is very short and does not approach the character limits that typically cause this error
    // n -> north, s -> south, etc
    @GetMapping("/within-bounds")
    public ResponseEntity<List<MinimalPlaceDto>> getWithinBounds(
            @RequestParam Double n,
            @RequestParam Double s,
            @RequestParam Double e,
            @RequestParam Double w,
            @RequestParam Double zoom,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Category category
            ) {

        return ResponseEntity.ok(placeService.getMinimalWithinBoundsPlaces(n, s, e, w, zoom,  query, category));
    }

    @GetMapping("category")
    public ResponseEntity<PaginatedResponse<PlaceDto>> getPlaceCategory(
            @RequestParam Category category,
            @RequestParam( required = false, defaultValue = "0") int page,
            @RequestParam( required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(placeService.getPlaceCategory(category, page, size));
    }


}


