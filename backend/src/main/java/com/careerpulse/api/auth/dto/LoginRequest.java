package com.careerpulse.api.auth.dto;

import com.careerpulse.api.common.validation.Utf8ByteLength;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(
                max = 255,
                message = "Email must not contain more than 255 characters"
        )
        String email,

        @NotBlank(message = "Password is required")
        @Size(
                max = 72,
                message = "Password must not contain more than 72 characters"
        )
        @Utf8ByteLength(
                max = 72,
                message = "Password must not exceed 72 UTF-8 bytes"
        )
        String password

) {
}