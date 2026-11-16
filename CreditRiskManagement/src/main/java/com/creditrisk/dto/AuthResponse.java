
package com.creditrisk.dto;

public record AuthResponse(
        String message,
        Integer userId,
        String name,
        String email,
        String role
) {
}
