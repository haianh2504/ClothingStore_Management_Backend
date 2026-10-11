package org.example.spring_backend_clothingstore.product.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProductPageResponse(
        List<CategoryProductResponse> categories,
        PageMetadata pagination
) {
    public static ProductPageResponse from(
            List<CategoryProductResponse> categories,
            Page<?> page
    ) {
        return new ProductPageResponse(
                categories,
                PageMetadata.from(page)
        );
    }
}
