package org.example.spring_backend_clothingstore.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email
        @NotBlank
        String email,

        @Size(min = 12, max = 128)
        @NotBlank
        String password
) {
}
