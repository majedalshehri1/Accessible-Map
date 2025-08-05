package com.main.app.service;

import com.main.app.model.Accessibility;
import com.main.app.repository.AccessibillityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessibillityService {


    private final AccessibillityRepository accessibillityRepository;


    public List<Accessibility> getAllAccessibillities() {
        return accessibillityRepository.findAll();
    }

    public Optional<Accessibility> getAccessibillityById(Long id) {
        return accessibillityRepository.findById(id);
    }

    public Accessibility createAccessibillity(Accessibility accessibility) {
        return accessibillityRepository.save(accessibility);
    }

    public Accessibility updatedAccessibillity(Long id, Accessibility updatedAccessibillity) {
        Accessibility existing = accessibillityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Accessibility not found with ID: " + id));

        existing.setType(updatedAccessibillity.getType());
        return accessibillityRepository.save(existing);
    }

    public void deleteAccessibillity(Long id) {
        if (!accessibillityRepository.existsById(id)) {
            throw new RuntimeException("Accessibillity not found with ID: " + id);
        }
        accessibillityRepository.deleteById(id);
    }

}
