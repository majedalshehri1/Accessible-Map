package com.main.app.service.User;

import com.main.app.Enum.Role;
import com.main.app.config.SecurityUtils;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.User.UserDto;
import com.main.app.model.User.User;
import com.main.app.repository.User.UserRepository;
import com.main.app.service.Security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .userName("testuser")
                .userEmail("test@example.com")
                .passwordHash("hashedpassword")
                .userRole(Role.USER)
                .isBlocked(false)
                .build();

        userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setUserName("testuser");
        userDto.setUserEmail("test@example.com");
        userDto.setHasRole(Role.USER);
        userDto.setIsBlocked(false);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertInstanceOf(CustomUserDetails.class, userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("hashedpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByUserEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("nonexistent@example.com"));
    }

    @Test
    void updateUsername_WithValidEmailAndNewUsername_ShouldUpdateUsername() {
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByUserName("newusername")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUsername("test@example.com", "newusername");

        assertNotNull(updatedUser);
        assertEquals("newusername", updatedUser.getUserName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUsername_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByUserEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.updateUsername("nonexistent@example.com", "newusername"));
    }

    @Test
    void updateUsername_WhenUsernameAlreadyExists_ShouldThrowException() {
        when(userRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByUserName("existingusername")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUsername("test@example.com", "existingusername"));
    }

    @Test
    void updateUsernameById_WithValidIdAndNewUsername_ShouldUpdateUsername() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUsernameById(1L, "newusername");

        assertNotNull(updatedUser);
        assertEquals("newusername", updatedUser.getUserName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUsernameById_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userService.updateUsernameById(999L, "newusername"));
    }

    @Test
    void convertToDTO_ShouldConvertUserToDtoCorrectly() {
        UserDto result = userService.convertToDTO(user);

        assertNotNull(result);
        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(user.getUserName(), result.getUserName());
        assertEquals(user.getUserEmail(), result.getUserEmail());
        assertEquals(user.getUserRole(), result.getHasRole());
        assertEquals(user.getIsBlocked(), result.getIsBlocked());
    }

    @Test
    void getAllUsers_ShouldReturnPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        PaginatedResponse<UserDto> result = userService.getAllUsers(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());
    }

    @Test
    void findOnlyUsers_ShouldReturnPaginatedResponseWithUsersOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findByUserRole(Role.USER, pageable)).thenReturn(userPage);

        PaginatedResponse<UserDto> result = userService.findOnlyUsers(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(Role.USER, result.getContent().get(0).getHasRole());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
    }
}