package org.example.spring_backend_clothingstore.product_variant.dto.response;

import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;

/** Dữ liệu variant được trả về sau khi tạo thành công. */
public record CreatedProductVariantResponse(
        Long productId,
        Long variantId,
        String color,
        String size,
        int quantity,
        String imageUrl
) {
    /** Chuyển entity variant thành response theo contract UC11. */
    public static CreatedProductVariantResponse from(ProductVariant variant) {
        return new CreatedProductVariantResponse(
                variant.getProduct().getId(), variant.getId(), variant.getColor(),
                variant.getSize().name(), variant.getQuantity(), variant.getImageUrl()
        );
    }
}
