package com.webchat.server.security;

import com.webchat.server.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JWTUtil {

    private static final String SECRET_KEY = "ZGVmYXVsdC1zZWNyZXQta2V5LXRoaXMtYXBwbGljYXRpb24td2lsbC1iZSB1c2VkLWZvci1qd3QtYXV0aGVudGljYXRpb24=";


    private final SecretKey key;

    public JWTUtil() {
        // Use the static secret key instead of generating one every time
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generate JWT token using UUID userId
    public String generateToken(UUID userId, int jwtTokenCode) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("jwtCode", jwtTokenCode);
        return createToken(claims, userId);
    }

    // Create the token
    private String createToken(Map<String, Object> claims, UUID userId) {
        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10 hours
                .signWith(key) // Updated to use byte[] for signing
                .compact();
    }

    // Create token with short expiration time
    public String createTokenShort(Map<String, Object> claims, UUID userId) {
        long expirationTime = 15 * 60 * 1000; // 15 minutes in milliseconds

        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    // Extract userId from token (UUID)
    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject)); // Convert String back to UUID
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public int extractJwtCode(String token) {
        return (int) extractClaim(token, claims -> claims.get("jwtCode"));
    }


    // Extract claims from the token
    private <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        JwtParser jwtParser = Jwts.parser() // Using Jwts.parser() instead of parserBuilder
                .verifyWith(key) // Updated to use byte[] for signing key
                .build();
        return jwtParser.parseSignedClaims(token).getPayload(); // `getBody()` is still available but check deprecation warnings
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate the token
    public boolean validateToken(String token, UUID userId) {
        return (userId.equals(extractUserId(token)) && !isTokenExpired(token));
    }

    public boolean validateTokenUser(String token, User user) {
        return (!isTokenExpired(token) && user.getJwtTokenCode() == extractJwtCode(token));
    }

    // Functional interface to extract a claim
    @FunctionalInterface
    interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
