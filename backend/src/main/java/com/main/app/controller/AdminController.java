package com.main.app.controller;

import com.main.app.dto.*;
import com.main.app.dto.Place.PlaceUpdatedDto;
import com.main.app.dto.Survey.SurveyResponseDTO;
import com.main.app.model.Place.Place;
import com.main.app.model.User.User;
import com.main.app.Enum.EntityType;
import com.main.app.dto.Admin.AdminLogDto;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.Place.PlaceDto;
import com.main.app.dto.Place.TopPlaceDto;
import com.main.app.dto.Review.ReviewRequestDTO;
import com.main.app.dto.Review.ReviewResponseDTO;
import com.main.app.dto.User.UserDto;
import com.main.app.service.Admin.AdminLogService;
import com.main.app.service.Admin.AdminService;
import com.main.app.service.Place.PlaceService;
import com.main.app.service.Review.ReviewService;
import com.main.app.service.Survey.SurveyService;
import com.main.app.service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    private final SurveyService surveyService;

    private final AdminLogService adminLogService;


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
    @GetMapping("/searchUser")
    public ResponseEntity<List<UserDto>> searchUserEmail(@RequestParam String email) {
        List<User> users = adminService.searchUserByEmail(email);

        List<UserDto> dtos1 = users.stream()
                .map(userService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos1);
    }

    @GetMapping("/findReview")
    public ReviewResponseDTO getReviewById(@RequestParam long reviewId) {
        return reviewService.getReviewById(reviewId);
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
    public ResponseEntity<PlaceDto> updatePlace(@PathVariable Long id,
                                                @RequestBody PlaceUpdatedDto dto) {
        var saved = adminService.adminUpdatePlace(id, dto);
        return ResponseEntity.ok(placeService.convertToDto(saved));
    }



    @DeleteMapping("delete/place/{id}")
    public ResponseEntity<String> deletePlace(@PathVariable Long id) {
        adminService.deletePlace(id);
        return ResponseEntity.ok("Place deleted successfully");
    }

    @PutMapping("/update/user/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        var saved = adminService.adminUpdateUser(id, dto);
        return ResponseEntity.ok(userService.convertToDTO(saved));
    }

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }



    @GetMapping("places/top")
    public ResponseEntity<List<TopPlaceDto>> getTopPlaces() {
        return ResponseEntity.ok(adminService.getTopPlaces(5));
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

    @GetMapping("/last24hours")
    public List<ReviewResponseDTO> getReviewsFromLast24Hours() {
        return reviewService.getLast24HoursReviews();
    }

    @GetMapping("/reviewsbycategory")
    public ResponseEntity<List<Object[]>> getReviewsByCategory() {
        return ResponseEntity.ok(reviewService.getReviewCountByCategory());
        }

    @GetMapping("count/placeCategory")
    public ResponseEntity<List<Object[]>> countPlaceCategory() {
        return ResponseEntity.ok(placeService.countPlacesByCategory());

    }

    @GetMapping("/logs")
    public ResponseEntity<PaginatedResponse<AdminLogDto>> getLogs(@RequestParam(required = false ) Integer page,
                                     @RequestParam(required = false ) Integer size,
                                     @RequestParam(required = false)EntityType entityType,
                                     @RequestParam(required = false)Long entityId) {
        return  ResponseEntity.ok(adminLogService.list(page, size, entityType, entityId));
    }
    @GetMapping("/all/reviews")
    public ResponseEntity<PaginatedResponse<ReviewResponseDTO>> allReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getAllReviews(page, size));
    }

    @GetMapping("/all/places")
    public ResponseEntity<PaginatedResponse<PlaceDto>> getAllPlaces(
            @RequestParam( required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(placeService.getAllPlaces(page, size));
    }

    @GetMapping("/all/users")
    public ResponseEntity<PaginatedResponse<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.findOnlyUsers(page, size));
    }

    @GetMapping("/survey/all")
    public ResponseEntity<List<SurveyResponseDTO>>  getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurvey());
    }

//    @PutMapping("/api/admin/survey/{id}/read")
//    public ResponseEntity<Void> setSurveyRead(
//            @PathVariable Long id,
//            @RequestBody Map<String, Boolean> body) {
//        boolean read = Boolean.TRUE.equals(body.get("read"));
//        surveyService.updateReadStatus(id, read);
//        return ResponseEntity.ok().build();
//    }


    @DeleteMapping("/survey/delete/{id}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.ok("Survey deleted successfully");
    }




}
