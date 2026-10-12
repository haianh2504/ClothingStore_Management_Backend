package org.example.spring_backend_clothingstore.product_variant.dto;

import org.example.spring_backend_clothingstore.product.entity.Product;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.example.spring_backend_clothingstore.api_response_schema.PageMetadata;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record ProductVariantResponse(
        String productName,
        String description,
        ProductCategory category,
        BigDecimal price,
        List<ProductVariantItemResponse> items,
        PageMetadata pagination
) {
    public ProductVariantResponse {
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(category, "Product category cannot be null");
        Objects.requireNonNull(price, "Product price cannot be null");
        Objects.requireNonNull(items, "Product variant items cannot be null");
        Objects.requireNonNull(pagination, "Pagination metadata cannot be null");
        items = List.copyOf(items);
    }

    public static ProductVariantResponse from(
            Product product,
            Page<ProductVariant> productVariants
    ) {
        Objects.requireNonNull(product, "Product cannot be null");
        Objects.requireNonNull(productVariants, "Product variants cannot be null");

        return new ProductVariantResponse(
                product.getName().name(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                productVariants.stream()
                        .map(ProductVariantItemResponse::from)
                        .toList(),
                PageMetadata.from(productVariants)
        );
    }
}
