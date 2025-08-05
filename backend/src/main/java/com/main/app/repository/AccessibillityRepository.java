package com.main.app.repository;

import com.main.app.model.Accessibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessibillityRepository extends JpaRepository<Accessibility, Long> {
}
