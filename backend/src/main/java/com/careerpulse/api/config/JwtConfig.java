package com.careerpulse.api.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    private static final int MINIMUM_KEY_LENGTH_BYTES = 32;

    @Bean
    public SecretKey jwtSigningKey(
            JwtProperties jwtProperties
    ) {
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(
                    jwtProperties.secret()
            );
        }
        catch (DecodingException exception) {
            throw new IllegalStateException(
                    "JWT secret must be valid Base64",
                    exception
            );
        }

        if (keyBytes.length < MINIMUM_KEY_LENGTH_BYTES) {
            throw new IllegalStateException(
                    "JWT secret must decode to at least "
                            + "32 bytes (256 bits)"
            );
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}