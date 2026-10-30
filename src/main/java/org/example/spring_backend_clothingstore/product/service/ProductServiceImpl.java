package org.example.spring_backend_clothingstore.product.service;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.exception.business.ProductNameAlreadyExistsException;
import org.example.spring_backend_clothingstore.product.dto.request.CreateProductRequest;
import org.example.spring_backend_clothingstore.product.dto.response.CategoryProductResponse;
import org.example.spring_backend_clothingstore.product.dto.response.CategoryProductPageResponse;
import org.example.spring_backend_clothingstore.product.dto.response.CreatedProductResponse;
import org.example.spring_backend_clothingstore.product.dto.response.ProductPageResponse;
import org.example.spring_backend_clothingstore.product.dto.response.ProductResponse;
import org.example.spring_backend_clothingstore.product.entity.Product;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product.entity.product_name.ProductName;
import org.example.spring_backend_clothingstore.product.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    // constructors with final elements are handled by @RequiredArgsConstructor

    /**
     * Tạo product và luôn đặt saleStatus ban đầu là INACTIVE.
     *
     * Pre-check giúp trả lỗi dễ hiểu; unique constraint trong database vẫn là
     * lớp bảo vệ cuối cùng khi có hai request tạo cùng tên đồng thời.
     */
    @Override
    @Transactional
    public CreatedProductResponse createProduct(CreateProductRequest request) {
        ProductName productName = new ProductName(request.productName());

        if (productRepository.existsByName_Name(productName.name())) {
            throw new ProductNameAlreadyExistsException(productName.name());
        }

        Product product = Product.create(
                productName,
                request.description().trim(),
                ProductCategory.valueOf(request.category()),
                request.basePrice()
        );

        try {
            return CreatedProductResponse.from(productRepository.saveAndFlush(product));
        } catch (DataIntegrityViolationException exception) {
            // Xử lý race condition mà pre-check không thể ngăn chặn hoàn toàn.
            throw new ProductNameAlreadyExistsException(productName.name());
        }
    }

    // Find all active products
    public ProductPageResponse findActiveProducts(Pageable pageable) {
        Page<Product> activeProducts = productRepository.findAllBySaleStatus(
                SaleStatus.ACTIVE,
                pageable
        );

        Map<ProductCategory, List<ProductResponse>> productsByCategory =
                activeProducts.getContent().stream()
                        .collect(Collectors.groupingBy(
                                Product::getCategory,
                                LinkedHashMap::new,
                                Collectors.mapping(
                                        ProductResponse::from,
                                        Collectors.toList()
                                )
                        ));

        List<CategoryProductResponse> categories = Arrays.stream(ProductCategory.values())
                .map(category -> CategoryProductResponse.from(
                        category,
                        productsByCategory.getOrDefault(category, List.of())
                ))
                .toList();

        return ProductPageResponse.from(categories, activeProducts);
    }

    // Find `ACTIVE` products of a specific category
    @Override
    public CategoryProductPageResponse findActiveProductsByCategory(
            Pageable pageable,
            ProductCategory category
    ) {
        Page<ProductResponse> products = productRepository
                .findAllByCategoryAndSaleStatus(category, SaleStatus.ACTIVE, pageable)
                .map(ProductResponse::from);

        return CategoryProductPageResponse.from(category, products);
    }
}
