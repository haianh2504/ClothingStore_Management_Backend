package org.example.spring_backend_clothingstore.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlaceOrderRequest(
        @NotBlank(message = "Customer name is required")
        @Size(max = 255, message = "Customer name must not exceed 255 characters")
        String customerName,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "Delivery address is required")
        @Size(max = 255, message = "Delivery address must not exceed 255 characters")
        String deliveryAddress
) {
}
