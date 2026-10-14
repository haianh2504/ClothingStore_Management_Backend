package org.example.spring_backend_clothingstore.product_variant.service;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.exception.business.ProductInactiveException;
import org.example.spring_backend_clothingstore.exception.resource.ProductNotFoundException;
import org.example.spring_backend_clothingstore.exception.resource.ProductVariantNotFoundException;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product.entity.Product;
import org.example.spring_backend_clothingstore.product.repository.ProductRepository;
import org.example.spring_backend_clothingstore.product_variant.dto.ProductVariantResponse;
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
    private final ProductRepository productRepository;

    // GET BY ID
    @Override
    public ProductVariant getProductVariantById(Long id)
    {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new ProductVariantNotFoundException(id));
    };
    // GET VARIANTS OF A PRODUCT
    @Override
    public ProductVariantResponse getProductVariantsByProductId(
            Long productId,
            Pageable pageable
    ) {
        if (productId == null || productId <= 0) {
            throw new ProductNotFoundException(productId);
        }

        Objects.requireNonNull(pageable,"pageable cannot be null");

        Page<ProductVariant> variants = productVariantRepository.findAllByProduct_IdAndProduct_SaleStatus(
                productId,
                SaleStatus.ACTIVE,
                pageable
        );

        Product product = variants.getContent().stream()
                .findFirst()
                .map(ProductVariant::getProduct)
                .orElseGet(() -> productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId)));

        if (product.getSaleStatus() != SaleStatus.ACTIVE) {
            throw new ProductInactiveException(productId);
        }

        return ProductVariantResponse.from(product, variants);
    };
}
