package org.example.spring_backend_clothingstore.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.exception.business.InsufficientStockException;
import org.example.spring_backend_clothingstore.exception.business.InvalidGuestIdException;
import org.example.spring_backend_clothingstore.exception.business.InvalidQuantityException;
import org.example.spring_backend_clothingstore.exception.business.MissingGuestIdException;
import org.example.spring_backend_clothingstore.exception.business.ProductInactiveForCartException;
import org.example.spring_backend_clothingstore.cart.entity.Cart;
import org.example.spring_backend_clothingstore.cart.repository.CartRepository;
import org.example.spring_backend_clothingstore.cart_item.entity.CartItem;
import org.example.spring_backend_clothingstore.cart_item.repository.CartItemRepository;
import org.example.spring_backend_clothingstore.cart.dto.response.CartResponse;
import org.example.spring_backend_clothingstore.exception.resource.CartItemNotFoundException;
import org.example.spring_backend_clothingstore.exception.resource.CartNotFoundException;
import org.example.spring_backend_clothingstore.exception.resource.ProductVariantNotFoundException;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.example.spring_backend_clothingstore.product_variant.repository.ProductVariantRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.Objects;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    @Override
    public AddCartItemResult addProductVariantToCart(String guestId, Long variantId, int quantity) {
        validateGuestId(guestId);

        if (variantId == null || variantId <= 0) {
            throw new ProductVariantNotFoundException(variantId);
        }

        if (quantity <= 0) {
            throw InvalidQuantityException.mustBePositive();
        }

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId));

        if (variant.getProduct().getSaleStatus() != SaleStatus.ACTIVE
                || variant.getDeletedAt() != null) {
            throw new ProductInactiveForCartException(variantId);
        }

        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseGet(() -> cartRepository.save(new Cart(guestId)));

        Optional<CartItem> existingCartItem = cartItemRepository
                .findByCart_IdAndVariant_Id(cart.getId(), variantId);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int requestedTotalQuantity = cartItem.getQuantity() + quantity;
            validateStock(requestedTotalQuantity, variant.getQuantity());
            cartItem.setQuantity(requestedTotalQuantity);
            cartItemRepository.save(cartItem);
            return new AddCartItemResult(toCartResponse(cart), false);
        }

        validateStock(quantity, variant.getQuantity());
        cartItemRepository.save(new CartItem(cart, variant, quantity));
        return new AddCartItemResult(toCartResponse(cart), true);
    }

    private void validateGuestId(String guestId) {
        if (guestId == null || guestId.isBlank()) {
            throw new MissingGuestIdException();
        }

        if (guestId.length() > 255) {
            throw new InvalidGuestIdException();
        }
    }

    private void validateStock(int requestedQuantity, int availableQuantity) {
        if (requestedQuantity > availableQuantity) {
            throw new InsufficientStockException(requestedQuantity, availableQuantity);
        }
    }

    private CartResponse toCartResponse(Cart cart) {
        return CartResponse.from(
                cart.getId(),
                cartItemRepository.findAllByCart_Id(
                        cart.getId(),
                        org.springframework.data.domain.Pageable.unpaged()
                )
        );
    }

    @Transactional(readOnly = true)
    @Override
    public CartResponse getCartByGuestId(String guestId, Pageable pageable) {
        validateGuestId(guestId);
        Objects.requireNonNull(pageable, "pageable cannot be null");

        return cartRepository.findByGuestId(guestId)
                .map(cart -> {
                    var items = cartItemRepository.findAllByCart_Id(cart.getId(), pageable);
                    return CartResponse.from(
                            cart.getId(),
                            items,
                            cartItemRepository.countByCart_Id(cart.getId()),
                            cartItemRepository.sumQuantityByCartId(cart.getId()),
                            cartItemRepository.sumSubtotalByCartId(cart.getId())
                    );
                })
                .orElseGet(() -> CartResponse.empty(
                        org.springframework.data.domain.Page.empty(pageable)
                ));
    }

    // UPDATE CART ITEM QUANTITY
    @Transactional
    @Override
    public UpdateCartItemResult updateCartItemQuantity(String guestId, Long cartItemId, int quantity) {
        validateGuestId(guestId);

        if (cartItemId == null || cartItemId <= 0) {
            throw new CartItemNotFoundException(cartItemId);
        }

        if (quantity < 1) {
            throw InvalidQuantityException.mustBeAtLeastOne();
        }

        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(CartNotFoundException::new);

        CartItem cartItem = cartItemRepository.findByIdAndCart_Id(cartItemId, cart.getId())
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        ProductVariant variant = cartItem.getVariant();
        if (variant.getProduct().getDeletedAt() != null
                || variant.getProduct().getSaleStatus() != SaleStatus.ACTIVE
                || variant.getDeletedAt() != null) {
            throw new ProductInactiveForCartException(
                    "Failed: Product [id=" + variant.getProduct().getId() + "] is no longer available"
            );
        }

        if (quantity > variant.getQuantity()) {
            throw new InsufficientStockException(quantity, variant.getQuantity());
        }

        boolean unchanged = cartItem.getQuantity() == quantity;
        if (!unchanged) {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return new UpdateCartItemResult(toCartResponse(cart), unchanged);
    }
}
