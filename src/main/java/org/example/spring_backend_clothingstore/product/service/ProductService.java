package org.example.spring_backend_clothingstore.product.service;

import org.example.spring_backend_clothingstore.product.dto.request.CreateProductRequest;
import org.example.spring_backend_clothingstore.product.dto.request.UpdateProductRequest;
import org.example.spring_backend_clothingstore.product.dto.response.CreatedProductResponse;
import org.example.spring_backend_clothingstore.product.dto.response.ProductPageResponse;
import org.example.spring_backend_clothingstore.product.dto.response.CategoryProductPageResponse;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    // Tạo product mới với saleStatus mặc định là INACTIVE.
    CreatedProductResponse createProduct(CreateProductRequest request);

    // Cập nhật một hoặc nhiều thông tin cơ bản của product.
    CreatedProductResponse updateProduct(Long productId, UpdateProductRequest request);

    // Find all ACTIVE products
    ProductPageResponse findActiveProducts(Pageable pageable);

    // Find all ACTIVE products by a specific category
    CategoryProductPageResponse findActiveProductsByCategory(Pageable pageable, ProductCategory category);
}
