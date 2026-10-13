package org.example.spring_backend_clothingstore.product.service;

import org.example.spring_backend_clothingstore.product.dto.response.ProductPageResponse;
import org.example.spring_backend_clothingstore.product.dto.response.CategoryProductPageResponse;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    // Find all ACTIVE products
    ProductPageResponse findActiveProducts(Pageable pageable);

    // Find all ACTIVE products by a specific category
    CategoryProductPageResponse findActiveProductsByCategory(Pageable pageable, ProductCategory category);
}
