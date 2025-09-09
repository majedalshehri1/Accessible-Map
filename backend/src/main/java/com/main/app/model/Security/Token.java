package com.main.app.model.Security;

import com.main.app.Enum.TokenType;
import com.main.app.model.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "token",
        indexes = @Index(name = "idx_token_user", columnList = "user_id"))
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String jti;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Instant issuedAt;
    private Instant expiresAt;
    private boolean revoked;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}