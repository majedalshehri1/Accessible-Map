package com.main.app.service.User;

import com.main.app.Enum.Role;
import com.main.app.dto.PaginatedResponse;
import com.main.app.dto.User.UserDto;
import com.main.app.model.User.User;
import com.main.app.repository.User.UserRepository;
import com.main.app.service.Security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name());

        return new CustomUserDetails(user, List.of(authority));
    }

    public User updateUsername(String userEmail, String newUsername) {
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (userRepository.existsByUserName(newUsername)) {
            throw new IllegalArgumentException("Username already in use");
        }

        user.setUserName(newUsername);
        return userRepository.save(user);
    }

    public UserDto convertToDTO(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setUserEmail(user.getUserEmail());
        dto.setHasRole(user.getUserRole());
        dto.setIsBlocked(user.getIsBlocked());
        return dto;
    }

    public PaginatedResponse<UserDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDto> userDtos = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                userDtos,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    public PaginatedResponse<UserDto> findOnlyUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByUserRole(Role.USER, pageable);

        List<UserDto> userDtos = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                userDtos,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

}
