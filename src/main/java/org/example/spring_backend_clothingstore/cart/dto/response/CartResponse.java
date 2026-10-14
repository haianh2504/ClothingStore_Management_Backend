package org.example.spring_backend_clothingstore.cart.dto.response;

import org.example.spring_backend_clothingstore.cart_item.entity.CartItem;

import java.util.List;
import java.util.Objects;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        int totalItems,
        int totalQuantity
) {
    public CartResponse {
        Objects.requireNonNull(cartId, "Cart id cannot be null");
        Objects.requireNonNull(items, "Cart items cannot be null");
        items = List.copyOf(items);
    }

    public static CartResponse from(Long cartId, List<CartItemResponse> items) {
        int totalQuantity = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        return new CartResponse(
                cartId,
                items,
                items.size(),
                totalQuantity
        );
    }

    public record CartItemResponse(
            Long cartItemId,
            Long variantId,
            int quantity
    ) {
        public CartItemResponse {
            Objects.requireNonNull(cartItemId, "Cart item id cannot be null");
            Objects.requireNonNull(variantId, "Variant id cannot be null");
        }

        public static CartItemResponse from(CartItem cartItem) {
            Objects.requireNonNull(cartItem, "Cart item cannot be null");

            return new CartItemResponse(
                    cartItem.getId(),
                    cartItem.getVariant().getId(),
                    cartItem.getQuantity()
            );
        }
    }
}
