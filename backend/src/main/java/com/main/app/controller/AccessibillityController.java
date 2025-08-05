package com.main.app.controller;

import com.main.app.model.Accessibillity;
import com.main.app.service.AccessibillityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accessibilities")
@RequiredArgsConstructor
public class AccessibillityController {

    private  final AccessibillityService accessibillityService;

    // Get All
    @GetMapping("/all")
    public ResponseEntity<List<Accessibillity>> getAllAccessibilities() {
        return ResponseEntity.ok(accessibillityService.getAllAccessibillities());
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Accessibillity> getAccessibillityById(@PathVariable Long id) {
        return accessibillityService.getAccessibillityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create
    @PostMapping("/create")
    public ResponseEntity<Accessibillity> create(@Valid @RequestBody Accessibillity accessibillity) {
        Accessibillity created = accessibillityService.createAccessibillity(accessibillity);
        return ResponseEntity.ok(created);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Accessibillity> updateAccessibillity(@PathVariable Long id, @Valid @RequestBody Accessibillity accessibillity) {
        Accessibillity updated = accessibillityService.updatedAccessibillity(id, accessibillity);
        return ResponseEntity.ok(updated);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Accessibillity> deleteAccessibillity(@PathVariable Long id) {
        accessibillityService.deleteAccessibillity(id);
        return ResponseEntity.noContent().build();
    }

}
