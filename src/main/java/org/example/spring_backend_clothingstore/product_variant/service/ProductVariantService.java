package org.example.spring_backend_clothingstore.product_variant.service;

import org.example.spring_backend_clothingstore.product_variant.dto.ProductVariantResponse;
import org.springframework.data.domain.Pageable;

public interface ProductVariantService {

    // GET VARIANTS OF A PRODUCT
    ProductVariantResponse getProductVariantsByProductId(
            Long productId,
            Pageable pageable
    );
}
