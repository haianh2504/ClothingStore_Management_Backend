package org.example.spring_backend_clothingstore.product_variant.service;

import org.example.spring_backend_clothingstore.product_variant.dto.ProductVariantResponse;
import org.example.spring_backend_clothingstore.product_variant.dto.request.AddProductVariantRequest;
import org.example.spring_backend_clothingstore.product_variant.dto.response.CreatedProductVariantResponse;
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

    /** Tạo variant mới cho product và lưu ảnh variant. */
    CreatedProductVariantResponse createProductVariant(Long productId, AddProductVariantRequest request);
}
