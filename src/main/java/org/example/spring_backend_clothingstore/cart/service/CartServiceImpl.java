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
import org.example.spring_backend_clothingstore.exception.resource.ProductVariantNotFoundException;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.example.spring_backend_clothingstore.product_variant.repository.ProductVariantRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

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

        if (variant.getProduct().getSaleStatus() != SaleStatus.ACTIVE) {
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
        var items = cartItemRepository.findAllByCart_Id(cart.getId()).stream()
                .map(CartResponse.CartItemResponse::from)
                .toList();

        return CartResponse.from(cart.getId(), items);
    }
}
