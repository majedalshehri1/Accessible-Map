package com.main.app.config;

import com.main.app.model.User.User;
import com.main.app.repository.User.UserRepository;
import com.main.app.service.Security.CustomUserDetails;
import com.main.app.service.Security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService,
                                   UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = extractTokenFromCookieOrHeader(request);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        if (path.contains("/v3/api-docs") || path.contains("/swagger")) {
            chain.doFilter(request, response);
            return;
        }
        if (!jwtService.isTokenValid(token)) {
            unauthorizedAndClearCookie(response);
            return;
        }

        String userEmail;
        try {
            userEmail = jwtService.extractUsername(token);
        } catch (Exception e) {
            unauthorizedAndClearCookie(response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (userDetails instanceof CustomUserDetails cud && cud.isBlocked()) {
                forbiddenAndClearCookie(response);
                return;
            }

            var userOpt = userRepository.findByUserEmail(userEmail);
            if (userOpt.isEmpty()) {
                unauthorizedAndClearCookie(response);
                return;
            }
            User u = userOpt.get();
            if (Boolean.TRUE.equals(u.getIsBlocked())) {
                forbiddenAndClearCookie(response);
                return;
            }

            var principal = new AuthUser(
                    u.getUserId(),
                    u.getUserName(),
                    u.getUserEmail(),
                    u.getUserRole()
            );

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + u.getUserRole()));

            var authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        chain.doFilter(request, response);
    }

    private static String extractTokenFromCookieOrHeader(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("jwt".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    return c.getValue();
                }
            }
        }
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    private static void unauthorizedAndClearCookie(HttpServletResponse res) throws IOException {
        ResponseCookie clear = ResponseCookie.from("jwt","")
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0).build();
        res.addHeader(HttpHeaders.SET_COOKIE, clear.toString());
        res.addHeader("Cache-Control", "no-store");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/problem+json");
        res.getWriter().write("""
          {"type":"about:blank","title":"Unauthorized","status":401,"detail":"Invalid or expired token"}
        """);
    }

    private static void forbiddenAndClearCookie(HttpServletResponse res) throws IOException {
        ResponseCookie clear = ResponseCookie.from("jwt","")
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0).build();
        res.addHeader(HttpHeaders.SET_COOKIE, clear.toString());
        res.addHeader("Cache-Control", "no-store");
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType("application/problem+json");
        res.getWriter().write("""
          {"type":"about:blank","title":"Forbidden","status":403,"detail":"User is blocked"}
        """);
    }
}