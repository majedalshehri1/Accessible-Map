package com.main.app.service;

import com.main.app.Enum.Role;
import com.main.app.dto.ReviewResponseDTO;
import com.main.app.dto.UserDto;
import com.main.app.model.Review;
import com.main.app.model.User;
import com.main.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public List<UserDto> getAllUser() {
        return userRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public List<UserDto> findOnlyUsers() {
        return userRepository.findOnlyUsers();

    }

}
