package org.example.spring_backend_clothingstore.product_variant.service;

import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductVariantService {

    // GET VARIANTS OF A PRODUCT
    Page<ProductVariant> findAllByProduct_IdAndProduct_SaleStatus(Long productId, SaleStatus saleStatus, Pageable pageable);
}
