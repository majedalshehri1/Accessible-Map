package com.main.app.controller;

import com.main.app.model.Accessibility;
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
    public ResponseEntity<List<Accessibility>> getAllAccessibilities() {
        return ResponseEntity.ok(accessibillityService.getAllAccessibillities());
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Accessibility> getAccessibillityById(@PathVariable Long id) {
        return accessibillityService.getAccessibillityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create
    @PostMapping("/create")
    public ResponseEntity<Accessibility> create(@Valid @RequestBody Accessibility accessibillity) {
        Accessibillity created = accessibillityService.createAccessibillity(accessibillity);
        return ResponseEntity.ok(created);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Accessibility> updateAccessibillity(@PathVariable Long id, @Valid @RequestBody Accessibility accessibillity) {
        Accessibility updated = accessibillityService.updatedAccessibillity(id, accessibillity);
        return ResponseEntity.ok(updated);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Accessibility> deleteAccessibillity(@PathVariable Long id) {
        accessibillityService.deleteAccessibillity(id);
        return ResponseEntity.noContent().build();
    }

}
