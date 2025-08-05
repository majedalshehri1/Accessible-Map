package com.main.app.controller;

import com.main.app.dto.PlaceDto;
import com.main.app.model.Place;
import com.main.app.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/place")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @GetMapping("/all")
    public ResponseEntity <List<Place>> getAllPlaces(){
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("{id}")
    public ResponseEntity<Optional<Place>> getPlaceById(@PathVariable Long id){
        return ResponseEntity.ok(placeService.getPlaceById(id));
    }

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

    @PutMapping("/update")
    public ResponseEntity<PlaceDto> updatePlace(@RequestBody PlaceDto dto) {
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

    @DeleteMapping("{id}")
    public void deletePlace(@PathVariable Long id){
           placeService.deletePlace(id);
    }



}
