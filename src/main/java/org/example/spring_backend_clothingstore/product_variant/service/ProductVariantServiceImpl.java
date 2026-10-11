package org.example.spring_backend_clothingstore.product_variant.service;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product.repository.ProductRepository;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.example.spring_backend_clothingstore.product_variant.repository.ProductVariantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;

    // GET VARIANTS OF A PRODUCT
    @Override
    public Page<ProductVariant> findAllByProduct_IdAndProduct_SaleStatus(Long productId, SaleStatus saleStatus, Pageable pageable) {
        Objects.requireNonNull(productId,"productId cannot be null");
        Objects.requireNonNull(saleStatus,"saleStatus cannot be null");
        return productVariantRepository.findAllByProduct_IdAndProduct_SaleStatus(productId, saleStatus, pageable);
    };
}
