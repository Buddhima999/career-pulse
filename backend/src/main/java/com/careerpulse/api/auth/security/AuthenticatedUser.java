package com.careerpulse.api.auth.security;

import com.careerpulse.api.user.entity.UserRole;

public record AuthenticatedUser(
        Long id,
        String email,
        UserRole role
) {
}