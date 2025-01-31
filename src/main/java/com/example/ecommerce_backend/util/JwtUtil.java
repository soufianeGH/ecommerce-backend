package com.example.ecommerce_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "qFh7cG8eQzJ5Yj9sKx4xN1BpYkZ0cGVjUThlUnFIRlU=";
    private static final long EXPIRATION = 24 * 60 * 60 * 1000; // 1 day

    private final Key key;

    public JwtUtil() {
        // Validate Base64 encoding
        try {
            byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
            this.key = Keys.hmacShaKeyFor(decodedKey);
            System.out.println("✅ Secret key is valid and properly decoded.");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("❌ Invalid Base64-encoded secret key.", e);
        }
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", "USER")  // ✅ Explicitly setting role
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                System.out.println("❌ JWT token is missing or empty.");
                return null;
            }

            System.out.println("🔍 Validating Token: [" + token + "]");

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return getEmailFromToken(token);
        } catch (JwtException e) {
            System.out.println("❌ JWT Validation Failed: " + e.getMessage());
            return null;
        }
    }

    public String getEmailFromToken(String token) {
        System.out.println("🔍 Received token: " + token);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("✅ Extracted Claims: " + claims);
            System.out.println("🔍 User Role: " + claims.get("role"));  // ✅ Log role

            return claims.getSubject();
        } catch (JwtException e) {
            System.out.println("❌ JWT Parsing Failed: " + e.getMessage());
            return null;
        }
    }


    /**
     * Extracts the email from the Authorization header.
     * @param authHeader The "Authorization" header (expected format: "Bearer <token>")
     * @return The email from the JWT or throws an exception if invalid.
     */
    public String extractEmailFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("❌ Missing or invalid Authorization header.");
        }

        String token = authHeader.substring(7).trim(); // ✅ Remove "Bearer " and trim spaces
        System.out.println("🔍 Extracted Token: [" + token + "]");

        String email = validateToken(token);
        if (email == null) {
            throw new RuntimeException("❌ Invalid JWT token.");
        }
        return email;
    }
}
