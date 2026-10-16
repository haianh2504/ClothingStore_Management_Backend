package org.example.spring_backend_clothingstore.cart.service;

import org.example.spring_backend_clothingstore.cart.entity.Cart;
import org.example.spring_backend_clothingstore.cart.dto.response.CartResponse;
import org.springframework.data.domain.Pageable;

public interface CartService {
    AddCartItemResult addProductVariantToCart(String guestId, Long variantId, int quantity);
    CartResponse getCartByGuestId(String guestId, Pageable pageable);
    UpdateCartItemResult updateCartItemQuantity(String guestId, Long cartItemId, int quantity);
}
