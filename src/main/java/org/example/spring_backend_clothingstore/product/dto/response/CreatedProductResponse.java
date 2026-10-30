package org.example.spring_backend_clothingstore.product.dto.response;

import org.example.spring_backend_clothingstore.product.entity.Product;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Response tối thiểu trả về sau khi tạo product thành công.
 */
public record CreatedProductResponse(
        Long productId,
        String productName,
        String description,
        String category,
        BigDecimal basePrice,
        String saleStatus
) {
    public CreatedProductResponse {
        Objects.requireNonNull(productId, "Product id cannot be null");
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(category, "Category cannot be null");
        Objects.requireNonNull(basePrice, "Base price cannot be null");
        Objects.requireNonNull(saleStatus, "Sale status cannot be null");
    }

    /** Chuyển entity Product đã lưu thành response theo contract UC09. */
    public static CreatedProductResponse from(Product product) {
        return new CreatedProductResponse(
                product.getId(),
                product.getName().name(),
                product.getDescription(),
                product.getCategory().name(),
                product.getPrice(),
                product.getSaleStatus().name()
        );
    }
}
