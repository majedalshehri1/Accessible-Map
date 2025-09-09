package com.main.app.service.Security;


import com.main.app.dto.Security.AuthRequest;
import com.main.app.dto.Security.AuthResponse;
import com.main.app.model.User.User;
import com.main.app.repository.User.UserRepository;
import com.main.app.Exceptions.LockedUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public record LoginResult(AuthResponse body, ResponseCookie cookie) {}

    public LoginResult login(AuthRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepository.findByUserEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new LockedUserException("User is blocked");
        }

        String jwt = jwtService.generateAccessToken(user);
        ResponseCookie cookie = buildJwtCookie(jwt);

        AuthResponse body = new AuthResponse(jwt, user.getUserId(), user.getUserName(), user.getUserEmail() , user.getUserRole());

        return new LoginResult(body, cookie);
    }

    public ResponseCookie buildJwtCookie(String token) {
        return ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60L * 60 * 24 * 30)
                .build();
    }
}
