package org.example.spring_backend_clothingstore.cart.service;

import org.example.spring_backend_clothingstore.cart.dto.response.CartResponse;

public record UpdateCartItemResult(
        CartResponse cart,
        boolean unchanged
) {
}
