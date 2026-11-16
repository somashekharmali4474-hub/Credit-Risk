
package com.creditrisk.service;

import com.creditrisk.dto.AuthResponse;
import com.creditrisk.dto.LoginRequest;
import com.creditrisk.dto.RegisterRequest;
import com.creditrisk.entity.User;
import com.creditrisk.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registration
    public AuthResponse register(RegisterRequest request) {

        if (request.name() == null ||
                request.name().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (request.email() == null ||
                !request.email().matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Enter a valid email");
        }

        if (request.password() == null ||
                request.password().length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        User user = new User();

        user.setName(request.name().trim());
        user.setEmail(email);

        // Store hashed password
        user.setPassword(
                passwordEncoder.encode(request.password()));

        // Public registration always creates a customer
        user.setRole("CUSTOMER");

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                "Registration successful",
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // Login
    public AuthResponse login(LoginRequest request) {

        if (request.email() == null ||
                request.password() == null) {
            throw new IllegalArgumentException(
                    "Email and password are required");
        }

        String email = request.email().trim().toLowerCase();

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.password(), user.getPassword())) {

            throw new IllegalArgumentException(
                    "Invalid email or password");
        }

        return new AuthResponse(
                "Login successful",
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
