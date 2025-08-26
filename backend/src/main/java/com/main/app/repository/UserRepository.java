package com.main.app.repository;

import com.main.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);
    boolean existsByUserEmail(String email);

    boolean existsByUserName(String newUsername);

    List<User> searchByUserEmailContainingIgnoreCase(String userEmail);
}