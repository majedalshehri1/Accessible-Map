package com.main.app.controller;

import com.main.app.model.Accessibility;
import com.main.app.service.AccessibilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accessibilities")
@RequiredArgsConstructor
public class AccessibillityController {

    @Autowired
    private AccessibilityService accessibilityService;

    // Get All
    @GetMapping("/all")
    public ResponseEntity<List<Accessibility>> getAllAccessibilities() {
        return ResponseEntity.ok(accessibilityService.getAllAccessibillities());
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Accessibility> getAccessibillityById(@PathVariable Long id) {
        return accessibilityService.getAccessibillityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create
    @PostMapping("/create")
    public ResponseEntity<Accessibility> create(@Valid @RequestBody Accessibility accessibillity) {
        Accessibility created = accessibilityService.createAccessibillity(accessibillity);
        return ResponseEntity.ok(created);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Accessibility> updateAccessibillity(@PathVariable Long id, @Valid @RequestBody Accessibility accessibility) {
        Accessibility updated = accessibilityService.updatedAccessibility(id, accessibility);
        return ResponseEntity.ok(updated);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Accessibility> deleteAccessibillity(@PathVariable Long id) {
        accessibilityService.deleteAccessibillity(id);
        return ResponseEntity.noContent().build();
    }

}
