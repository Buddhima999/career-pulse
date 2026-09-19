package com.careerpulse.api.auth.service;

import com.careerpulse.api.auth.dto.RegisterRequest;
import com.careerpulse.api.auth.dto.UserResponse;
import com.careerpulse.api.auth.exception.EmailAlreadyRegisteredException;
import com.careerpulse.api.user.entity.User;
import com.careerpulse.api.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedFullName = request.fullName()
                .trim()
                .replaceAll("\\s+", " ");

        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException();
        }

        String passwordHash =
                passwordEncoder.encode(request.password());

        User user = new User(
                normalizedFullName,
                normalizedEmail,
                passwordHash
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getStatus()
        );
    }
}