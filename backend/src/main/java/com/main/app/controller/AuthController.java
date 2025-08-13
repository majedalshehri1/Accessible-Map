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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

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


    private ResponseCookie buildJwtCookie(String token) {
        return ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60L * 60 * 24 * 30)
                .build();
    }

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
    ResponseCookie cookie = buildJwtCookie(jwtToken);
    AuthResponse body = new AuthResponse(
            jwtToken,
            savedUser.getUserId(),
            savedUser.getUserName(),
            savedUser.getUserEmail()
    );
    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(body);

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
        ResponseCookie cookie = buildJwtCookie(jwtToken);
        AuthResponse body = new AuthResponse(
                jwtToken,
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail()
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie clear = ResponseCookie.from("jwt", "")
                .httpOnly(true).secure(true).sameSite("None").path("/")
                .maxAge(0) // remobve the cookie
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clear.toString())
                .build();
    }
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        User user = userRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        return ResponseEntity.ok(Map.of(
                "id", user.getUserId(),
                "username", user.getUserName(),
                "email", user.getUserEmail()
        ));
    }
}