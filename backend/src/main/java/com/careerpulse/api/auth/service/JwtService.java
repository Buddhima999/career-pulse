package com.careerpulse.api.auth.service;

import com.careerpulse.api.config.JwtProperties;
import com.careerpulse.api.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final String ISSUER = "career-pulse-api";

    private final SecretKey signingKey;
    private final JwtProperties jwtProperties;

    public JwtService(
            SecretKey signingKey,
            JwtProperties jwtProperties
    ) {
        this.signingKey = signingKey;
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(User user) {
        Instant issuedAt = Instant.now();

        Instant expiresAt = issuedAt.plusSeconds(
                jwtProperties.expirationSeconds()
        );

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationSeconds() {
        return jwtProperties.expirationSeconds();
    }
}