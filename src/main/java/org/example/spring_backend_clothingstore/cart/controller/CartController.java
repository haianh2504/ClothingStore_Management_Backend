package org.example.spring_backend_clothingstore.cart.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.example.spring_backend_clothingstore.cart.dto.request.AddCartItemRequest;
import org.example.spring_backend_clothingstore.cart.dto.request.UpdateCartItemRequest;
import org.example.spring_backend_clothingstore.cart.dto.response.CartResponse;
import org.example.spring_backend_clothingstore.cart.service.AddCartItemResult;
import org.example.spring_backend_clothingstore.cart.service.CartService;
import org.example.spring_backend_clothingstore.cart.service.UpdateCartItemResult;
import org.example.spring_backend_clothingstore.guest.GuestIdResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final GuestIdResolver guestIdResolver;

    @PostMapping("/carts/items")
    public ResponseEntity<ApiResponse<CartResponse>> addProductVariantToCart(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        String guestId = guestIdResolver.resolve(httpRequest, httpResponse);
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
    @GetMapping("/carts")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByGuestId(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Pageable pageable
    ) {
        String guestId = guestIdResolver.resolve(httpRequest, httpResponse);
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

    @PatchMapping("/cartItems/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemQuantity(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        String guestId = guestIdResolver.resolve(httpRequest, httpResponse);
        UpdateCartItemResult result = cartService.updateCartItemQuantity(
                guestId,
                cartItemId,
                request.quantity()
        );

        int status = HttpStatus.OK.value();
        String code = result.unchanged()
                ? "CART_ITEM_UNCHANGED"
                : "CART_ITEM_QUANTITY_UPDATED";
        String message = result.unchanged()
                ? "Cart item [id=" + cartItemId + "] quantity remains unchanged"
                : "Cart item [id=" + cartItemId + "] quantity updated successfully";

        return ResponseEntity.ok(ApiResponse.from(
                status,
                code,
                message,
                OffsetDateTime.now(),
                result.cart()
        ));
    }

    @DeleteMapping("/cartItems/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            @PathVariable Long cartItemId
    ) {
        String guestId = guestIdResolver.resolve(httpRequest, httpResponse);
        CartResponse cart = cartService.removeCartItem(guestId, cartItemId);
        int status = HttpStatus.OK.value();

        return ResponseEntity.ok(ApiResponse.from(
                status,
                "CART_ITEM_DELETED",
                "Cart item [id=" + cartItemId + "] is deleted successfully",
                OffsetDateTime.now(),
                cart
        ));
    }
}
