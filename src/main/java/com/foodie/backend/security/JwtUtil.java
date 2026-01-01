package com.foodie.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY =
            "foodie-secret-key-foodie-secret-key-123456";

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /* =========================
       GENERATE TOKEN
    ========================= */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of("role", role))
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
                )
                .signWith(key)
                .compact();
    }

    /* =========================
       PARSE CLAIMS
    ========================= */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /* =========================
       AUTH FOR WEBSOCKET / HTTP
    ========================= */
    public Authentication getAuthentication(String token) {

        String email = extractEmail(token);
        String role = extractRole(token);

        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
