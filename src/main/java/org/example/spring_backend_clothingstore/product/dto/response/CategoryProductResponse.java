package org.example.spring_backend_clothingstore.product.dto.response;

import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;

import java.util.List;
import java.util.Objects;

public record CategoryProductResponse(
        ProductCategory category,
        List<ProductResponse> items
) {
    public CategoryProductResponse{
        Objects.requireNonNull(category, "Product category cannot be null");
    }
    public static CategoryProductResponse from(ProductCategory category, List<ProductResponse> items) {
        return new CategoryProductResponse(category, items);
    }
}
