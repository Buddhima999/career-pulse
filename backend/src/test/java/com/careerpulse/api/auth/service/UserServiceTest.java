package com.careerpulse.api.auth.service;

import com.careerpulse.api.auth.dto.RegisterRequest;
import com.careerpulse.api.auth.dto.UserResponse;
import com.careerpulse.api.auth.exception.EmailAlreadyRegisteredException;
import com.careerpulse.api.user.entity.User;
import com.careerpulse.api.user.entity.UserRole;
import com.careerpulse.api.user.entity.UserStatus;
import com.careerpulse.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest(
                "  Buddhima    Dishantha  ",
                "  Buddhima@Example.COM  ",
                "StrongPass123!"
        );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "buddhima@example.com"
                )
        ).thenReturn(false);

        when(
                passwordEncoder.encode("StrongPass123!")
        ).thenReturn("{bcrypt}encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.register(request);

        assertEquals("Buddhima Dishantha", response.fullName());
        assertEquals("buddhima@example.com", response.email());
        assertEquals(UserRole.USER, response.role());
        assertEquals(UserStatus.ACTIVE, response.status());

        verify(passwordEncoder).encode("StrongPass123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectAlreadyRegisteredEmail() {
        RegisterRequest request = new RegisterRequest(
                "Buddhima Dishantha",
                "Buddhima@Example.COM",
                "StrongPass123!"
        );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "buddhima@example.com"
                )
        ).thenReturn(true);

        assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> userService.register(request)
        );

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }
}