package org.example.spring_backend_clothingstore.product.dto.response;

import org.example.spring_backend_clothingstore.api_response_schema.PageMetadata;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

public record CategoryProductPageResponse(
        ProductCategory category,
        List<ProductResponse> items,
        PageMetadata pagination
) {
    public CategoryProductPageResponse {
        Objects.requireNonNull(category, "Product category cannot be null");
        Objects.requireNonNull(items, "Product items cannot be null");
        Objects.requireNonNull(pagination, "Pagination cannot be null");
    }

    public static CategoryProductPageResponse from(
            ProductCategory category,
            Page<ProductResponse> products
    ) {
        return new CategoryProductPageResponse(
                category,
                products.getContent(),
                PageMetadata.from(products)
        );
    }
}
