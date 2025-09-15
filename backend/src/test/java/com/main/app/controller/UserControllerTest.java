package com.main.app.controller;

import com.main.app.config.AuthUser;
import com.main.app.config.SecurityUtils;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.User.UpdateUsernameDTO;
import com.main.app.dto.User.UserDto;
import com.main.app.model.User.User;
import com.main.app.service.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.main.app.Enum.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private User user;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .userName("testuser")
                .userEmail("test@example.com")
                .passwordHash("hashedpassword")
                .userRole(USER)
                .isBlocked(false)
                .build();

        authUser = new AuthUser(1L, "testuser", "test@example.com", USER);
    }

    @Test
    void updateUsername_WithValidRequest_ShouldReturnUpdatedUser() {
        UpdateUsernameDTO dto = new UpdateUsernameDTO();
        dto.setNewUsername("newusername");

        when(SecurityUtils.currentUser()).thenReturn(Optional.of(authUser));
        when(userService.updateUsernameById(1L, "newusername")).thenReturn(user);

        ResponseEntity<Map<String, Object>> response = userController.updateUsername(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().get("id"));
        assertEquals("testuser", response.getBody().get("username"));
        verify(userService, times(1)).updateUsernameById(1L, "newusername");
    }

    @Test
    void updateUsername_WhenUserNotAuthenticated_ShouldThrowException() {
        UpdateUsernameDTO dto = new UpdateUsernameDTO();
        dto.setNewUsername("newusername");

        when(SecurityUtils.currentUser()).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userController.updateUsername(dto));
    }

    @Test
    void getAllUsers_WithPagination_ShouldReturnPaginatedResponse() {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setUserName("testuser");
        userDto.setUserEmail("test@example.com");
        userDto.setHasRole(USER);
        userDto.setIsBlocked(false);

        PaginatedResponse<UserDto> paginatedResponse = new PaginatedResponse<>(
                List.of(userDto), 0, 10, 1L, 1, true
        );

        when(userService.getAllUsers(0, 10)).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<UserDto>> response = userController.getAllUsers(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(0, response.getBody().getCurrentPage());
        assertEquals(10, response.getBody().getPageSize());
        verify(userService, times(1)).getAllUsers(0, 10);
    }

    @Test
    void getAllUsers_WithDefaultPagination_ShouldUseDefaults() {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setUserName("testuser");
        userDto.setUserEmail("test@example.com");
        userDto.setHasRole(USER);
        userDto.setIsBlocked(false);

        PaginatedResponse<UserDto> paginatedResponse = new PaginatedResponse<>(
                List.of(userDto), 0, 10, 1L, 1, true
        );

        when(userService.getAllUsers(0, 10)).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<UserDto>> response = userController.getAllUsers(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).getAllUsers(0, 10);
    }

    @Test
    void getAllUsers_WithCustomPagination_ShouldUseCustomValues() {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setUserName("testuser");
        userDto.setUserEmail("test@example.com");
        userDto.setHasRole(USER);
        userDto.setIsBlocked(false);

        PaginatedResponse<UserDto> paginatedResponse = new PaginatedResponse<>(
                List.of(userDto), 2, 5, 1L, 1, true
        );

        when(userService.getAllUsers(2, 5)).thenReturn(paginatedResponse);

        ResponseEntity<PaginatedResponse<UserDto>> response = userController.getAllUsers(2, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).getAllUsers(2, 5);
    }
}