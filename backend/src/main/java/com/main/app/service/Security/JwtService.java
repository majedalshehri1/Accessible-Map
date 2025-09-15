package com.main.app.service.Security;

import com.main.app.model.User.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean isTokenValid(String token);
    String extractUsername(String token);
    String extractJti(String token);
}