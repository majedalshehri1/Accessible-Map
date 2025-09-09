package com.main.app.config;

import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static Optional<AuthUser> currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return Optional.empty();
        var p = auth.getPrincipal();
        return (p instanceof AuthUser au) ? Optional.of(au) : Optional.empty();
    }
}