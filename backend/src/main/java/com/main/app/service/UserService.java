package com.main.app.service;

import com.main.app.model.User;
import com.main.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getUserEmail(),
                user.getPasswordHash(),
                Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().name()))
        );

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

}