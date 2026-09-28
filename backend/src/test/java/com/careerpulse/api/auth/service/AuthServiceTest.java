package com.careerpulse.api.auth.service;

import com.careerpulse.api.auth.dto.LoginRequest;
import com.careerpulse.api.auth.dto.LoginResponse;
import com.careerpulse.api.auth.exception.AccountDisabledException;
import com.careerpulse.api.auth.exception.InvalidCredentialsException;
import com.careerpulse.api.user.entity.User;
import com.careerpulse.api.user.entity.UserStatus;
import com.careerpulse.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginWithValidCredentials() {
        LoginRequest request = new LoginRequest(
                "  Buddhima@Example.COM  ",
                "StrongPass123!"
        );

        User user = new User(
                "Buddhima Dishantha",
                "buddhima@example.com",
                "{bcrypt}encoded-password"
        );

        when(
                userRepository.findByEmailIgnoreCase(
                        "buddhima@example.com"
                )
        ).thenReturn(Optional.of(user));

        when(
                passwordEncoder.matches(
                        "StrongPass123!",
                        "{bcrypt}encoded-password"
                )
        ).thenReturn(true);

        when(jwtService.generateAccessToken(user))
                .thenReturn("test-access-token");

        when(jwtService.getExpirationSeconds())
                .thenReturn(3600L);

        LoginResponse response = authService.login(request);

        assertEquals(
                "test-access-token",
                response.accessToken()
        );
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertEquals(
                "Buddhima Dishantha",
                response.user().fullName()
        );
        assertEquals(
                "buddhima@example.com",
                response.user().email()
        );

        verify(userRepository).findByEmailIgnoreCase(
                "buddhima@example.com"
        );
        verify(passwordEncoder).matches(
                "StrongPass123!",
                "{bcrypt}encoded-password"
        );
        verify(jwtService).generateAccessToken(user);
    }

    @Test
    void shouldRejectLoginWhenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest(
                "  Missing@Example.COM  ",
                "StrongPass123!"
        );

        when(
                userRepository.findByEmailIgnoreCase(
                        "missing@example.com"
                )
        ).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmailIgnoreCase(
                "missing@example.com"
        );

        verifyNoInteractions(passwordEncoder);
        verify(jwtService, never())
                .generateAccessToken(any(User.class));
    }

    @Test
    void shouldRejectLoginWhenPasswordIsIncorrect() {
        LoginRequest request = new LoginRequest(
                "buddhima@example.com",
                "WrongPassword123!"
        );

        User user = new User(
                "Buddhima Dishantha",
                "buddhima@example.com",
                "{bcrypt}encoded-password"
        );

        when(
                userRepository.findByEmailIgnoreCase(
                        "buddhima@example.com"
                )
        ).thenReturn(Optional.of(user));

        when(
                passwordEncoder.matches(
                        "WrongPassword123!",
                        "{bcrypt}encoded-password"
                )
        ).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder).matches(
                "WrongPassword123!",
                "{bcrypt}encoded-password"
        );

        verify(jwtService, never())
                .generateAccessToken(any(User.class));
    }

    @Test
    void shouldRejectLoginWhenAccountIsDisabled() {
        LoginRequest request = new LoginRequest(
                "disabled@example.com",
                "StrongPass123!"
        );

        User user = mock(User.class);

        when(user.getPasswordHash())
                .thenReturn("{bcrypt}encoded-password");

        when(user.getStatus())
                .thenReturn(UserStatus.DISABLED);

        when(
                userRepository.findByEmailIgnoreCase(
                        "disabled@example.com"
                )
        ).thenReturn(Optional.of(user));

        when(
                passwordEncoder.matches(
                        "StrongPass123!",
                        "{bcrypt}encoded-password"
                )
        ).thenReturn(true);

        assertThrows(
                AccountDisabledException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder).matches(
                "StrongPass123!",
                "{bcrypt}encoded-password"
        );

        verify(jwtService, never())
                .generateAccessToken(any(User.class));
    }
}