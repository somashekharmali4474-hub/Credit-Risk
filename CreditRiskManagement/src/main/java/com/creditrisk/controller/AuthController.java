
package com.creditrisk.controller;

import com.creditrisk.dto.AuthResponse;
import com.creditrisk.dto.LoginRequest;
import com.creditrisk.dto.RegisterRequest;
import com.creditrisk.service.AuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register customer
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {
            AuthResponse response =
                    authService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        try {
            AuthResponse response =
                    authService.login(request);

            // Store authenticated user in server session
            session.setAttribute("userId", response.userId());
            session.setAttribute("userRole", response.role());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Check login session
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first"));
        }

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "role", session.getAttribute("userRole")
        ));
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully"));
    }
}
