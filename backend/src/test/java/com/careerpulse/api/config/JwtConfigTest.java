package com.careerpulse.api.config;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtConfigTest {

    private final JwtConfig jwtConfig = new JwtConfig();

    @Test
    void shouldCreateSigningKeyFromValidBase64Secret() {
        String secret = Base64.getEncoder()
                .encodeToString(new byte[32]);

        JwtProperties properties = new JwtProperties(
                secret,
                3600L
        );

        SecretKey signingKey =
                jwtConfig.jwtSigningKey(properties);

        assertNotNull(signingKey);
        assertEquals(
                32,
                signingKey.getEncoded().length
        );
    }

    @Test
    void shouldRejectMalformedBase64Secret() {
        JwtProperties properties = new JwtProperties(
                "%%%not-valid-base64%%%",
                3600L
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> jwtConfig.jwtSigningKey(properties)
        );

        assertEquals(
                "JWT secret must be valid Base64",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectSecretShorterThan256Bits() {
        String secret = Base64.getEncoder()
                .encodeToString(new byte[16]);

        JwtProperties properties = new JwtProperties(
                secret,
                3600L
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> jwtConfig.jwtSigningKey(properties)
        );

        assertEquals(
                "JWT secret must decode to at least "
                        + "32 bytes (256 bits)",
                exception.getMessage()
        );
    }
}