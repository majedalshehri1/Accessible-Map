package com.main.app.Repository.User;

import com.main.app.Enum.Role;
import com.main.app.dto.User.UserDto;
import com.main.app.model.User.User;
import com.main.app.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserEmail_WhenEmailExists_ShouldReturnUser() {
        // Given
        User user = User.builder()
                .userName("testuser")
                .userEmail("test@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();
        User savedUser = userRepository.save(user);

        // When
        Optional<User> result = userRepository.findByUserEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(savedUser.getUserId(), result.get().getUserId());
        assertEquals("test@example.com", result.get().getUserEmail());
    }

    @Test
    void findByUserEmail_WhenEmailDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<User> result = userRepository.findByUserEmail("nonexistent@example.com");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByUserEmail_WhenEmailExists_ShouldReturnTrue() {
        // Given
        User user = User.builder()
                .userName("testuser")
                .userEmail("test@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUserEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByUserEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByUserEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void existsByUserName_WhenUsernameExists_ShouldReturnTrue() {
        // Given
        User user = User.builder()
                .userName("testuser")
                .userEmail("test@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUserName("testuser");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByUserName_WhenUsernameDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByUserName("nonexistentuser");

        // Then
        assertFalse(exists);
    }

    @Test
    void searchByUserEmailContainingIgnoreCase_WhenEmailContainsText_ShouldReturnUsers() {
        // Given
        User user1 = User.builder()
                .userName("user1")
                .userEmail("test1@example.com")
                .passwordHash("hashedpassword1")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        User user2 = User.builder()
                .userName("user2")
                .userEmail("test2@example.com")
                .passwordHash("hashedpassword2")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        userRepository.saveAll(List.of(user1, user2));

        // When
        List<User> result = userRepository.searchByUserEmailContainingIgnoreCase("example");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getUserEmail().toLowerCase().contains("example")));
    }

    @Test
    void searchByUserEmailContainingIgnoreCase_WhenEmailDoesNotContainText_ShouldReturnEmptyList() {
        // Given
        User user = User.builder()
                .userName("testuser")
                .userEmail("test@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();
        userRepository.save(user);

        // When
        List<User> result = userRepository.searchByUserEmailContainingIgnoreCase("gmail");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_WithPageable_ShouldReturnPaginatedResults() {
        // Given
        for (int i = 1; i <= 15; i++) {
            User user = User.builder()
                    .userName("user" + i)
                    .userEmail("user" + i + "@example.com")
                    .passwordHash("hashedpassword" + i)
                    .userRole(Role.USER)
                    .isBlocked(false)
                    .build();
            userRepository.save(user);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertFalse(result.isLast());
    }

    @Test
    void findByUserRole_WithPageable_ShouldReturnPaginatedResults() {
        // Given
        User user1 = User.builder()
                .userName("user1")
                .userEmail("user1@example.com")
                .passwordHash("hashedpassword1")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        User user2 = User.builder()
                .userName("user2")
                .userEmail("user2@example.com")
                .passwordHash("hashedpassword2")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        User admin = User.builder()
                .userName("admin")
                .userEmail("admin@example.com")
                .passwordHash("hashedadmin")
                .userRole(Role.ADMIN)
                .isBlocked(false)
                .build();

        userRepository.saveAll(List.of(user1, user2, admin));

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByUserRole(Role.USER, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.getContent().stream().allMatch(u -> u.getUserRole() == Role.USER));
    }

    @Test
    void findOnlyUsers_ShouldReturnUserDtosWithUserRoleOnly() {
        // Given
        User user1 = User.builder()
                .userName("user1")
                .userEmail("user1@example.com")
                .passwordHash("hashedpassword1")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        User user2 = User.builder()
                .userName("user2")
                .userEmail("user2@example.com")
                .passwordHash("hashedpassword2")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        User admin = User.builder()
                .userName("admin")
                .userEmail("admin@example.com")
                .passwordHash("hashedadmin")
                .userRole(Role.ADMIN)
                .isBlocked(false)
                .build();

        userRepository.saveAll(List.of(user1, user2, admin));

        // When
        List<UserDto> result = userRepository.findOnlyUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.getHasRole() == Role.USER));
        assertTrue(result.stream().anyMatch(dto -> dto.getUserName().equals("user1")));
        assertTrue(result.stream().anyMatch(dto -> dto.getUserName().equals("user2")));
    }

    @Test
    void save_ShouldPersistUser() {
        // Given
        User user = User.builder()
                .userName("newuser")
                .userEmail("newuser@example.com")
                .passwordHash("newpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser.getUserId());
        assertEquals("newuser", savedUser.getUserName());
        assertEquals("newuser@example.com", savedUser.getUserEmail());
        assertEquals(Role.USER, savedUser.getUserRole());
        assertFalse(savedUser.getIsBlocked());
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // Given
        User user = User.builder()
                .userName("testuser")
                .userEmail("test@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();
        User savedUser = userRepository.save(user);

        // When
        Optional<User> result = userRepository.findById(savedUser.getUserId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(savedUser.getUserId(), result.get().getUserId());
        assertEquals("testuser", result.get().getUserName());
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<User> result = userRepository.findById(999L);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        // Given
        User user = User.builder()
                .userName("tobedeleted")
                .userEmail("delete@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();
        User savedUser = userRepository.save(user);

        // When
        userRepository.deleteById(savedUser.getUserId());

        // Then
        Optional<User> result = userRepository.findById(savedUser.getUserId());
        assertTrue(result.isEmpty());
    }

    @Test
    void testUserBlockedStatus() {
        // Given
        User blockedUser = User.builder()
                .userName("blockeduser")
                .userEmail("blocked@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(true)
                .build();

        User activeUser = User.builder()
                .userName("activeuser")
                .userEmail("active@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        userRepository.saveAll(List.of(blockedUser, activeUser));

        // When
        List<User> allUsers = userRepository.findAll();

        // Then
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(User::getIsBlocked));
        assertTrue(allUsers.stream().anyMatch(u -> !u.getIsBlocked()));
    }
}