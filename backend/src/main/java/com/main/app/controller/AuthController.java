package com.main.app.controller;

import com.main.app.Exceptions.DuplicateEmailException;
import com.main.app.Exceptions.DuplicateUsernameException;
import com.main.app.config.SecurityUtils;
import com.main.app.dto.Security.AuthRequest;
import com.main.app.model.User.User;
import com.main.app.dto.Security.AuthResponse;
import com.main.app.dto.User.RegisterRequest;
import com.main.app.repository.User.UserRepository;
import com.main.app.service.Security.AuthService;
import com.main.app.service.Security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final AuthService authService;



@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    if (userRepository.existsByUserEmail(request.getEmail())) {
        throw new DuplicateEmailException(request.getEmail());
    }

    if (userRepository.existsByUserName(request.getUsername())) {
        throw new DuplicateUsernameException(request.getUsername());
    }


    User user = new User();
    user.setUserName(request.getUsername());
    user.setUserEmail(request.getEmail());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

    User savedUser = userRepository.save(user);

    String jwtToken = jwtService.generateAccessToken(savedUser);
    ResponseCookie cookie = authService.buildJwtCookie(jwtToken);
    AuthResponse body = new AuthResponse(
            jwtToken,
            savedUser.getUserId(),
            savedUser.getUserName(),
            savedUser.getUserEmail(),
            savedUser.getUserRole()
    );
    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(body);

}

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        var result = authService.login(req);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.cookie().toString())
                .body(result.body());
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
    public ResponseEntity<Map<String, Object>> me() {

        var me = SecurityUtils.currentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return ResponseEntity.ok(Map.of(
                "id",       me.id(),
                "username", me.name(),
                "email",    me.email(),
                "role",     me.role()
        ));
    }



}
