package org.example.spring_backend_clothingstore.cart.service;

import org.example.spring_backend_clothingstore.cart.dto.response.CartResponse;

import java.util.Objects;

public record AddCartItemResult(
        CartResponse cart,
        boolean created
) {
    public AddCartItemResult {
        Objects.requireNonNull(cart, "Cart response cannot be null");
    }
}
