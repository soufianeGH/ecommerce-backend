package com.example.ecommerce_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}") // Default: 1 day in milliseconds
    private long jwtExpirationInMs;

    private Key key;

    @PostConstruct
    public void init() {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secretKey.trim());
            this.key = Keys.hmacShaKeyFor(decodedKey);
            System.out.println("Secret key is valid and properly decoded.");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64-encoded secret key.", e);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", "USER")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndExtractEmail(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject();
        } catch (JwtException e) {
            System.out.println("JWT validation/parsing failed: " + e.getMessage());
            return null;
        }
    }

    // Helper method to extract the token from the Authorization header.
    public String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header.");
        }
        return authHeader.substring(7).trim();
    }
}
