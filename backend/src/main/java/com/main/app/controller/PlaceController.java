package com.main.app.controller;

import com.main.app.dto.PlaceDto;
import com.main.app.model.Place;
import com.main.app.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    private PlaceService placeService;
    @Operation(summary = "Get all places")

    @GetMapping("/all")
    public ResponseEntity <List<Place>> getAllPlaces(){
        return ResponseEntity.ok(placeService.getAllPlaces());
    }
    @Operation(summary = "Get place by ID")

    @GetMapping("{id}")
    public ResponseEntity<Optional<Place>> getPlaceById(@PathVariable Long id){
        return ResponseEntity.ok(placeService.getPlaceById(id));
    }
    @Operation(summary = "Create a new place")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Place created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/create")
    public ResponseEntity<PlaceDto> createPlace(@RequestBody PlaceDto dto) {
        Place place = new Place();
        place.setPlaceName(dto.getPlaceName());
        place.setLatitude(dto.getLatitude());
        place.setLongitude(dto.getLongitude());
        place.setPlaceCategory(dto.getCategory());

        Place saved = placeService.createPlace(place);

        PlaceDto response = new PlaceDto();
        response.setPlaceName(saved.getPlaceName());
        response.setLatitude(saved.getLatitude());
        response.setLongitude(saved.getLongitude());
        response.setCategory(saved.getPlaceCategory());

        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Delete a place")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Place deleted"),
            @ApiResponse(responseCode = "404", description = "Place not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not owner")
    })
    @DeleteMapping("/{id}")
    public void deletePlace(@PathVariable Long id){
        placeService.deletePlace(id);
    }



}
