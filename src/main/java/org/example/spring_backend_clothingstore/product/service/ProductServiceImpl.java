package org.example.spring_backend_clothingstore.product.service;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.product.dto.response.CategoryProductResponse;
import org.example.spring_backend_clothingstore.product.dto.response.ProductPageResponse;
import org.example.spring_backend_clothingstore.product.dto.response.ProductResponse;
import org.example.spring_backend_clothingstore.product.entity.Product;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
