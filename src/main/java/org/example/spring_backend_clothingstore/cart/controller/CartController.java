package org.example.spring_backend_clothingstore.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.example.spring_backend_clothingstore.cart.dto.request.AddCartItemRequest;
import org.example.spring_backend_clothingstore.cart.dto.response.CartResponse;
import org.example.spring_backend_clothingstore.cart.service.AddCartItemResult;
import org.example.spring_backend_clothingstore.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addProductVariantToCart(
            @RequestHeader("X-Guest-Id") String guestId,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        AddCartItemResult result = cartService.addProductVariantToCart(
                guestId,
                request.variantId(),
                request.quantity()
        );

        boolean created = result.created();
        int status = created ? HttpStatus.CREATED.value() : HttpStatus.OK.value();
        String code = created ? "CART_ITEM_CREATED" : "EXISTING_CART_ITEM_UPDATED";
        String message = created
                ? "Product added to cart successfully"
                : "Cart item updated successfully";

        return ResponseEntity.status(status).body(ApiResponse.from(
                status,
                code,
                message,
                OffsetDateTime.now(),
                result.cart()
        ));
    }

    // VIEW CART
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByGuestId(
            @RequestHeader("X-Guest-Id") String guestId,
            Pageable pageable
    ) {
        CartResponse cart = cartService.getCartByGuestId(guestId, pageable);
        boolean empty = cart.items().isEmpty();
        int status = HttpStatus.OK.value();

        return ResponseEntity.ok(ApiResponse.from(
                status,
                empty ? "CART_EMPTY" : "OK",
                empty ? "Empty cart" : "Get cart with items successfully",
                OffsetDateTime.now(),
                cart
        ));
    }
}
