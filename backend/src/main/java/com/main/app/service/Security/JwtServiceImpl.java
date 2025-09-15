package com.main.app.service.Security;

import com.main.app.Enum.TokenType;
import com.main.app.Exceptions.JwtValidationException;
import com.main.app.model.Security.Token;
import com.main.app.model.User.User;
import com.main.app.repository.Security.TokenRepository;
import com.main.app.config.BootId;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final TokenRepository tokenRepository;
    private final BootId bootId;
    private static final long ACCESS_TTL_MS = Duration.ofHours(2).toMillis();

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException("Invalid jwt.secret (must be Base64 of >=256-bit key)", e);
        }
    }

    @Override
    public String generateAccessToken(User user) {
        return generateToken(new HashMap<>(), user, TokenType.ACCESS, ACCESS_TTL_MS);
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user, TokenType.REFRESH, 7L * 24 * 60 * 60 * 1000);
    }

    private String generateToken(Map<String,Object> extra, User user, TokenType type, long expMs) {
        extra.put("type", type.name());
        extra.put("boot", bootId.get());

        return Jwts.builder()
                .setClaims(extra)
                .setSubject(user.getUserEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expMs))
                .setId(java.util.UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parse(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new JwtValidationException("JWT parse/verify failed", e);
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims c = parse(token);
            String boot = c.get("boot", String.class);
            boolean notRevoked = !isTokenRevoked(c.getId());
            boolean notExpired = c.getExpiration() != null && c.getExpiration().after(new Date());
            return notExpired && notRevoked && bootId.get().equals(boot);
        } catch (JwtValidationException e) {
            return false;
        }
    }

    private boolean isTokenRevoked(String jti) {
        return tokenRepository.findByJti(jti).map(Token::isRevoked).orElse(false);
    }

    @Override public String extractUsername(String token) { return parse(token).getSubject(); }
    @Override public String extractJti(String token)       { return parse(token).getId(); }
}
