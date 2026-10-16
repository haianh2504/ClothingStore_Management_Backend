package org.example.spring_backend_clothingstore.cart.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
        @NotNull(message = "Quantity is required")
        Integer quantity
) {
}
