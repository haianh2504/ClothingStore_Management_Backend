package org.example.spring_backend_clothingstore.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.spring_backend_clothingstore.api_response_schema.PageMetadata;
import org.example.spring_backend_clothingstore.cart_item.entity.CartItem;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        BigDecimal subTotal,
        int totalItems,
        int totalQuantity,
        PageMetadata pagination
) {
    public CartResponse {
        Objects.requireNonNull(items, "Cart items cannot be null");
        Objects.requireNonNull(subTotal, "Cart subtotal cannot be null");
        Objects.requireNonNull(pagination, "Pagination metadata cannot be null");
        items = List.copyOf(items);
    }

    public static CartResponse from(Long cartId, List<CartItemResponse> items) {
        return from(
                cartId,
                items,
                new PageMetadata(0, items.size(), items.size(), items.isEmpty() ? 0 : 1, true, true)
        );
    }

    public static CartResponse from(Long cartId, Page<CartItem> cartItems) {
        return from(
                cartId,
                cartItems,
                cartItems.getTotalElements(),
                cartItems.getContent().stream()
                        .mapToLong(CartItem::getQuantity)
                        .sum(),
                cartItems.getContent().stream()
                        .map(CartItemResponse::from)
                        .map(CartItemResponse::lineTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    public static CartResponse from(
            Long cartId,
            Page<CartItem> cartItems,
            long totalItems,
            long totalQuantity,
            BigDecimal subTotal
    ) {
        Objects.requireNonNull(cartItems, "Cart items page cannot be null");
        return new CartResponse(
                cartId,
                cartItems.getContent().stream().map(CartItemResponse::from).toList(),
                subTotal,
                Math.toIntExact(totalItems),
                Math.toIntExact(totalQuantity),
                PageMetadata.from(cartItems)
        );
    }

    private static CartResponse from(
            Long cartId,
            List<CartItemResponse> items,
            PageMetadata pagination
    ) {
        int totalQuantity = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        BigDecimal subTotal = items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cartId,
                items,
                subTotal,
                items.size(),
                totalQuantity,
                pagination
        );
    }

    public static CartResponse empty(Page<?> page) {
        return new CartResponse(
                null,
                List.of(),
                BigDecimal.ZERO,
                0,
                0,
                PageMetadata.from(page)
        );
    }

    public record CartItemResponse(
            Long cartItemId,
            Long variantId,
            String saleStatus,
            String productName,
            String imageUrl,
            String color,
            String size,
            BigDecimal unitPrice,
            int quantity,
            int availableQuantity,
            BigDecimal lineTotal,
            String availability
    ) {
        public CartItemResponse {
            Objects.requireNonNull(cartItemId, "Cart item id cannot be null");
            Objects.requireNonNull(variantId, "Variant id cannot be null");
        }

            public static CartItemResponse from(CartItem cartItem) {
            Objects.requireNonNull(cartItem, "Cart item cannot be null");

            var variant = cartItem.getVariant();
            var product = variant.getProduct();
            SaleStatus saleStatus = product.getSaleStatus();
            String availability = saleStatus == SaleStatus.INACTIVE
                    ? "PRODUCT_INACTIVE"
                    : cartItem.getQuantity() > variant.getQuantity()
                    ? "STOCK_INSUFFICIENT"
                    : "AVAILABLE";
            BigDecimal unitPrice = product.getPrice();

            return new CartItemResponse(
                    cartItem.getId(),
                    variant.getId(),
                    saleStatus.name(),
                    product.getName().name(),
                    variant.getImageUrl(),
                    variant.getColor(),
                    variant.getSize().name(),
                    unitPrice,
                    cartItem.getQuantity(),
                    variant.getQuantity(),
                    unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())),
                    availability
            );
        }
    }
}
