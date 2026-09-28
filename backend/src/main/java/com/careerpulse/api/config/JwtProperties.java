package com.careerpulse.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(

        @NotBlank(message = "JWT secret is required")
        String secret,

        @Positive(message = "JWT expiration must be greater than zero")
        long expirationSeconds

) {
}