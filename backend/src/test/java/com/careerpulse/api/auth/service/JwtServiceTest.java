package com.careerpulse.api.auth.service;

import com.careerpulse.api.config.JwtProperties;
import com.careerpulse.api.user.entity.User;
import com.careerpulse.api.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    @Test
    void shouldGenerateValidAccessToken() {
        SecretKey signingKey = Keys.hmacShaKeyFor(
                "12345678901234567890123456789012".getBytes()
        );

        JwtProperties jwtProperties = new JwtProperties(
                "unused-test-secret",
                3600L
        );

        JwtService jwtService = new JwtService(
                signingKey,
                jwtProperties
        );

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getEmail())
                .thenReturn("buddhima@example.com");
        when(user.getRole()).thenReturn(UserRole.USER);

        String token = jwtService.generateAccessToken(user);

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

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
}