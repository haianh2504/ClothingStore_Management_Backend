package org.example.spring_backend_clothingstore.product_variant.dto;

import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;

import java.util.Objects;

public record ProductVariantItemResponse(
        Long variantId,
        String color,
        String size,
        int quantity,
        String imageUrl
) {
    public ProductVariantItemResponse {
        Objects.requireNonNull(variantId, "Variant id cannot be null");
        Objects.requireNonNull(color, "Color cannot be null");
        Objects.requireNonNull(size, "Size cannot be null");
        Objects.requireNonNull(imageUrl, "Image URL cannot be null");
    }

    public static ProductVariantItemResponse from(ProductVariant productVariant) {
        Objects.requireNonNull(productVariant, "Product variant cannot be null");

        return new ProductVariantItemResponse(
                productVariant.getId(),
                productVariant.getColor(),
                productVariant.getSize().name(),
                productVariant.getQuantity(),
                productVariant.getImageUrl()
        );
    }
}
