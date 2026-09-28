package com.careerpulse.api.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public SecretKey jwtSigningKey(
            JwtProperties jwtProperties
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(
                jwtProperties.secret()
        );

        return Keys.hmacShaKeyFor(keyBytes);
    }
}