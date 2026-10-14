package org.example.spring_backend_clothingstore.product_variant.service;

import org.example.spring_backend_clothingstore.product_variant.dto.ProductVariantResponse;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.springframework.data.domain.Pageable;

public interface ProductVariantService {
    // Get variant by id
    ProductVariant getProductVariantById(Long id);
    // GET VARIANTS OF A PRODUCT
    ProductVariantResponse getProductVariantsByProductId(
            Long productId,
            Pageable pageable
    );
}
