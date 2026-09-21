package com.careerpulse.api.auth.dto;

import com.careerpulse.api.user.entity.UserRole;
import com.careerpulse.api.user.entity.UserStatus;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        UserStatus status
) {
}
