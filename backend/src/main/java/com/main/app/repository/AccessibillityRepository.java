package com.main.app.repository;

import com.main.app.model.Accessibillity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessibillityRepository extends JpaRepository<Accessibillity, Long> {
}
