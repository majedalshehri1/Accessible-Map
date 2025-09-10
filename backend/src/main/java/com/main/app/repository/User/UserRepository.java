package com.main.app.repository.User;

import com.main.app.Enum.Role;
import com.main.app.dto.User.UserDto;
import com.main.app.model.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<User> findAll(Pageable pageable);
    Page<User> findByUserRole(Role role, Pageable pageable);

    @Query("""
       select new com.main.app.dto.User.UserDto(
           u.userId, u.userName, u.userEmail, u.userRole as hasRole, u.isBlocked
       )
       from User u
       where upper(u.userRole) = 'USER'
       """)
    List<UserDto> findOnlyUsers();
    //where u.userRole = com.main.app.Enum.Role.USER
}