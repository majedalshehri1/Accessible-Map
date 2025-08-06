//package com.main.app.service;
//
//import com.main.app.model.Accessibility;
//import com.main.app.repository.AccessibilityRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class AccessibilityService {
//
//    private final AccessibilityRepository accessibilityRepository;
//
//
//    public List<Accessibility> getAllAccessibillities() {
//        return accessibilityRepository.findAll();
//    }
//
//    public Optional<Accessibility> getAccessibillityById(Long id) {
//        return accessibilityRepository.findById(id);
//    }
//
//    public Accessibility createAccessibillity(Accessibility accessibility) {
//        return accessibilityRepository.save(accessibility);
//    }
//
//    public Accessibility updatedAccessibility(Long id, Accessibility updatedAccessibility) {
//        Accessibility existing = accessibilityRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Accessibility not found with ID: " + id));
//
//        existing.setType(updatedAccessibility.getType());
//        return accessibilityRepository.save(existing);
//    }
//
//    public void deleteAccessibillity(Long id) {
//        if (!accessibilityRepository.existsById(id)) {
//            throw new RuntimeException("Accessibillity not found with ID: " + id);
//        }
//        accessibilityRepository.deleteById(id);
//    }
//
//}
