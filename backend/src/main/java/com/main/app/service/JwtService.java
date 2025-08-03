package com.main.app.service;

import com.main.app.model.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean isTokenValid(String token);
    String extractUsername(String token);
    String extractJti(String token);
}