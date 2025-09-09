package com.main.app.repository.User;

import com.main.app.dto.User.UserDto;
import com.main.app.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);
    boolean existsByUserEmail(String email);

    boolean existsByUserName(String newUsername);

    List<User> searchByUserEmailContainingIgnoreCase(String userEmail);

    @Query("""
           select new com.main.app.dto.User.UserDto(
               u.userId, u.userName, u.userEmail, u.userRole, u.isBlocked
           )
           from User u
           where upper(u.userRole) = 'USER'
           """)
    List<UserDto> findOnlyUsers();

}