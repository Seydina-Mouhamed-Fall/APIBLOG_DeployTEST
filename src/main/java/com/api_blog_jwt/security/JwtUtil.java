package com.api_blog_jwt.security;

import com.api_blog_jwt.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String jwtSecret = "MaSuperCleJWTdeSecuriteHyperComplexe123!@#";
    private final long jwtExpirationMs = 86400000; // 24h

    // Génère la clé de signature à partir de la chaîne secrète
    private Key getSignKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Génère un token JWT pour un utilisateur donné
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("roles", user.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setSubject(user.getEmail()) // subject = email
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrait l'email (subject) du token
    public String extractUsername(String token ){
        return extractAllClaims(token).getSubject();
    }

    // Extrait les claims (id, roles, etc.)
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Vérifie si le token a expiré
    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Vérifie si le token est valide pour cet utilisateur
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}

