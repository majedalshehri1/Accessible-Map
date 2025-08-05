package com.main.app.repository;

import com.main.app.Enum.AccessibillityType;
import com.main.app.model.Accessibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessibilityRepository extends JpaRepository<Accessibility, Long> {
    Optional<Accessibility> findByType(AccessibillityType type);
}
