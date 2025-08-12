package com.main.app.controller;

import com.main.app.Enum.Role;
import com.main.app.dto.AuthRequest;
import com.main.app.model.User;
import com.main.app.dto.AuthResponse;
import com.main.app.dto.RegisterRequest;
import com.main.app.repository.UserRepository;
import com.main.app.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    
//    @PostMapping("/register")
//    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
//        if (userRepository.existsByUserEmail(request.getEmail())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, " Email already in use");
//        }
//
//        User user = new User();
//        user.setUserName(request.getUsername());
//        user.setUserEmail(request.getEmail());
//        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
//
//        User savedUser = userRepository.save(user);
//
//        String jwtToken = jwtService.generateAccessToken(savedUser);
//
//        return ResponseEntity.ok(new AuthResponse(jwtToken));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//
//        User user = userRepository.findByUserEmail(request.getEmail())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        String jwtToken = jwtService.generateAccessToken(user);
//        return ResponseEntity.ok(new AuthResponse(jwtToken));
//    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUserEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        User user = new User();
        user.setUserName(request.getUsername());
        user.setUserEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        String jwtToken = jwtService.generateAccessToken(savedUser);

        return ResponseEntity.ok(new AuthResponse(jwtToken, savedUser.getUserName(), savedUser.getUserEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUserEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String jwtToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(new AuthResponse(jwtToken, user.getUserName(), user.getUserEmail()));
    }
}