package dev.cnpe.m6finalsc.application.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import static java.lang.System.currentTimeMillis;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                   .subject(userDetails.getUsername())
                   .issuedAt(new Date(currentTimeMillis()))
                   .expiration(new Date(currentTimeMillis() + 1000 * 60 * 60 * 24))
                   .claim("roles", userDetails.getAuthorities())
                   .signWith(getSignInKey())
                   .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .keyLocator(map -> getSignInKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
