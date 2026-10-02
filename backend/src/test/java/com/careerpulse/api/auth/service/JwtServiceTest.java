package com.careerpulse.api.auth.service;

import com.careerpulse.api.config.JwtProperties;
import com.careerpulse.api.user.entity.User;
import com.careerpulse.api.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    @Test
    void shouldGenerateAndParseValidAccessToken() {
        SecretKey signingKey = createSigningKey();

        JwtService jwtService = createJwtService(
                signingKey,
                3600L
        );

        User user = createUser();

        String token = jwtService.generateAccessToken(user);

        Claims claims = jwtService.parseAccessToken(token);

        assertNotNull(token);
        assertEquals("career-pulse-api", claims.getIssuer());
        assertEquals("1", claims.getSubject());
        assertEquals(
                "buddhima@example.com",
                claims.get("email", String.class)
        );
        assertEquals(
                "USER",
                claims.get("role", String.class)
        );
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertEquals(
                3600L,
                jwtService.getExpirationSeconds()
        );
    }

    @Test
    void shouldRejectTokenSignedWithDifferentKey() {
        SecretKey tokenSigningKey = createSigningKey();

        SecretKey differentKey = Keys.hmacShaKeyFor(
                "abcdefghijklmnopqrstuvwxyz123456"
                        .getBytes()
        );

        JwtService tokenCreator = createJwtService(
                tokenSigningKey,
                3600L
        );

        JwtService tokenVerifier = createJwtService(
                differentKey,
                3600L
        );

        String token = tokenCreator.generateAccessToken(
                createUser()
        );

        assertThrows(
                SignatureException.class,
                () -> tokenVerifier.parseAccessToken(token)
        );
    }

    @Test
    void shouldRejectExpiredAccessToken() {
        SecretKey signingKey = createSigningKey();

        JwtService jwtService = createJwtService(
                signingKey,
                3600L
        );

        Instant currentTime = Instant.now();

        String expiredToken = Jwts.builder()
                .issuer("career-pulse-api")
                .subject("1")
                .issuedAt(
                        Date.from(
                                currentTime.minusSeconds(120)
                        )
                )
                .expiration(
                        Date.from(
                                currentTime.minusSeconds(60)
                        )
                )
                .signWith(signingKey)
                .compact();

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.parseAccessToken(expiredToken)
        );
    }

    private SecretKey createSigningKey() {
        return Keys.hmacShaKeyFor(
                "12345678901234567890123456789012"
                        .getBytes()
        );
    }

    private JwtService createJwtService(
            SecretKey signingKey,
            long expirationSeconds
    ) {
        JwtProperties jwtProperties = new JwtProperties(
                "unused-test-secret",
                expirationSeconds
        );

        return new JwtService(
                signingKey,
                jwtProperties
        );
    }

    private User createUser() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getEmail())
                .thenReturn("buddhima@example.com");
        when(user.getRole()).thenReturn(UserRole.USER);

        return user;
    }
}