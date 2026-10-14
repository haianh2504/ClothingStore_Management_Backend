package org.example.spring_backend_clothingstore.cart.service;

public interface CartService {
    AddCartItemResult addProductVariantToCart(String guestId, Long variantId, int quantity);
}
