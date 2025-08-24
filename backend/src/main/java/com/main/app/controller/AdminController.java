package com.main.app.controller;

import com.main.app.Enum.Category;
import com.main.app.dto.*;
import com.main.app.model.Place;
import com.main.app.model.Review;
import com.main.app.model.User;
import com.main.app.service.AdminService;
import com.main.app.service.PlaceService;
import com.main.app.service.ReviewService;
import com.main.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ReviewService reviewService;
    private final PlaceService placeService;
    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/all/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> allReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/all/places")
    public ResponseEntity<List<PlaceDto>> getAllPlaces(){
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("all/users")
    public ResponseEntity <List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/count/places")
    public ResponseEntity<Long> countPlaces() {
        return ResponseEntity.ok(adminService.countAllPlaces());
    }
    @GetMapping("/count/reviews")
    public ResponseEntity<Long> countReviews() {
        return ResponseEntity.ok(adminService.countAllReviews());
    }
    @GetMapping("count/users")
    public ResponseEntity<Long> countUsers() {
        return ResponseEntity.ok(adminService.countAllUsers());
    }

    @GetMapping("/findPlace")
    public ResponseEntity<List<PlaceDto>> searchPlace(@RequestParam String search) {
        List<Place> places = placeService.searchPlace(search);

        List<PlaceDto> dtos = places.stream()
                .map(placeService::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/update/review/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long id, @RequestBody ReviewRequestDTO dto) {
        var saved = adminService.adminUpdateReview(id, dto);
        return ResponseEntity.ok(reviewService.convertToDTO(saved));

    }

    @DeleteMapping("/delete/review/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }

    @PutMapping("/update/place/{id}")
    public ResponseEntity<PlaceDto> updatePlace(@PathVariable Long id, @RequestBody PlaceDto dto) {
        var saved = adminService.adminUpdatePlace(id, dto);
        return ResponseEntity.ok(placeService.convertToDto(saved));
    }

    @DeleteMapping("delete/place/{id}")
    public ResponseEntity<String> deletePlace(@PathVariable Long id) {
        adminService.deletePlace(id);
        return ResponseEntity.ok("Place deleted successfully");
    }

    @GetMapping("places/top")
    public ResponseEntity<List<TopPlaceDto>> getTopPlaces() {
        return ResponseEntity.ok(adminService.getTopPlaces(3));
    }

    @PutMapping("users/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        adminService.blockUser(id);
        return ResponseEntity.ok("User is blocked successfully");
    }
    @PutMapping("users/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long id) {
        adminService.unblockUser(id);
        return ResponseEntity.ok("User is unblocked successfully");
    }

    @GetMapping("count/placeCategory")
    public ResponseEntity<List<Object[]>> countPlaceCategory() {
        return ResponseEntity.ok(placeService.countPlacesByCategory());

    }




}
