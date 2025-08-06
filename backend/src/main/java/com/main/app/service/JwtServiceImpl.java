package com.main.app.service;

import com.main.app.Enum.TokenType;
import com.main.app.model.Token;
import com.main.app.model.User;
import com.main.app.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final TokenRepository tokenRepository;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println("JWT Secret Key: " + Encoders.BASE64.encode(key.getEncoded()));
    }

    @Override
    public String generateAccessToken(User user) {
        return generateToken(new HashMap<>(), user, TokenType.ACCESS, 15 * 60 * 1000);
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user, TokenType.REFRESH, 7 * 24 * 60 * 60 * 1000);
    }

    private String generateToken(Map<String, Object> extraClaims, User user, TokenType tokenType, long expiration) {
        String jti = java.util.UUID.randomUUID().toString();
        extraClaims.put("type", tokenType.name());

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUserEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setId(jti)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            return !isTokenRevoked(extractJti(token)) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private boolean isTokenRevoked(String jti) {
        return tokenRepository.findByJti(jti)
                .map(Token::isRevoked)
                .orElse(true);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}