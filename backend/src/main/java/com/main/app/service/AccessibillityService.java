package com.main.app.service;

import com.main.app.model.Accessibillity;
import com.main.app.repository.AccessibillityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessibillityService {


    private final AccessibillityRepository accessibillityRepository;


    public List<Accessibillity> getAllAccessibillities() {
        return accessibillityRepository.findAll();
    }

    public Optional<Accessibillity> getAccessibillityById(Long id) {
        return accessibillityRepository.findById(id);
    }

    public Accessibillity createAccessibillity(Accessibillity accessibillity) {
        return accessibillityRepository.save(accessibillity);
    }

    public Accessibillity updatedAccessibillity(Long id, Accessibillity updatedAccessibillity) {
        Accessibillity existing = accessibillityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Accessibillity not found with ID: " + id));

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
