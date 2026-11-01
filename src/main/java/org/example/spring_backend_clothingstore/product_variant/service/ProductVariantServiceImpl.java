package org.example.spring_backend_clothingstore.product_variant.service;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.exception.business.ProductVariantAlreadyExistsException;
import org.example.spring_backend_clothingstore.exception.business.ProductVariantInformationException;
import org.example.spring_backend_clothingstore.exception.business.ProductInactiveException;
import org.example.spring_backend_clothingstore.exception.resource.ProductNotFoundException;
import org.example.spring_backend_clothingstore.exception.resource.ProductVariantNotFoundException;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product.entity.Product;
import org.example.spring_backend_clothingstore.product.repository.ProductRepository;
import org.example.spring_backend_clothingstore.product_variant.dto.ProductVariantResponse;
import org.example.spring_backend_clothingstore.product_variant.dto.request.AddProductVariantRequest;
import org.example.spring_backend_clothingstore.product_variant.dto.response.CreatedProductVariantResponse;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductSize;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.example.spring_backend_clothingstore.product_variant.repository.ProductVariantRepository;
import org.example.spring_backend_clothingstore.product_variant.storage.VariantImageStorage;
import org.example.spring_backend_clothingstore.product_variant.storage.VariantImageValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final VariantImageStorage variantImageStorage;
    private final VariantImageValidator variantImageValidator;

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

        Page<ProductVariant> variants = productVariantRepository.findAllByProduct_IdAndProduct_SaleStatusAndDeletedAtIsNull(
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

    /** Kiểm tra dữ liệu, lưu ảnh và tạo variant trong cùng một nghiệp vụ. */
    @Override
    @Transactional
    public CreatedProductVariantResponse createProductVariant(
            Long productId,
            AddProductVariantRequest request
    ) {
        if (productId == null || productId <= 0) {
            throw new ProductNotFoundException(productId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        ProductSize size = parseSize(request.getSize());
        String color = request.getColor().trim();
        variantImageValidator.validate(request.getImage());

        if (productVariantRepository.existsByProduct_IdAndSizeAndColorAndDeletedAtIsNull(productId, size, color)) {
            throw new ProductVariantAlreadyExistsException();
        }

        String imageUrl = null;
        try {
            imageUrl = variantImageStorage.store(request.getImage());
            ProductVariant variant = ProductVariant.create(
                    product, size, request.getQuantity(), color, imageUrl
            );
            ProductVariant savedVariant = productVariantRepository.saveAndFlush(variant);
            return CreatedProductVariantResponse.from(savedVariant);
        } catch (DataIntegrityViolationException exception) {
            variantImageStorage.deleteQuietly(imageUrl);
            throw new ProductVariantAlreadyExistsException();
        } catch (IOException exception) {
            variantImageStorage.deleteQuietly(imageUrl);
            throw new IllegalStateException("Unable to store product variant image", exception);
        } catch (RuntimeException exception) {
            variantImageStorage.deleteQuietly(imageUrl);
            throw exception;
        }
    }

    /** Chuyển size text sang enum và trả lỗi contract nếu giá trị không hợp lệ. */
    private ProductSize parseSize(String size) {
        try {
            return ProductSize.valueOf(size.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ProductVariantInformationException(List.of(
                    Map.of("field", "size", "message", "Invalid size")
            ));
        }
    }
}
