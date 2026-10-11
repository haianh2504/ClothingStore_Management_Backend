package org.example.spring_backend_clothingstore.product.dto.response;

import org.example.spring_backend_clothingstore.product.entity.Product;

import java.math.BigDecimal;
import java.util.Objects;

public record ProductResponse(
        Long productId,
        String productName,
        String description,
        BigDecimal unitPrice
) {
    public ProductResponse{
        Objects.requireNonNull(productId, "Product id cannot be null");
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(unitPrice, "Unit price cannot be null");
    }
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName().name(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
