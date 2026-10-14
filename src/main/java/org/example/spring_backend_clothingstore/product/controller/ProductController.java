package org.example.spring_backend_clothingstore.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.example.spring_backend_clothingstore.exception.business.InvalidProductCategoryException;
import org.example.spring_backend_clothingstore.exception.business.ProductCategoryRequiredException;
import org.example.spring_backend_clothingstore.product.dto.response.CategoryProductPageResponse;
import org.example.spring_backend_clothingstore.product.dto.response.ProductPageResponse;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.example.spring_backend_clothingstore.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // GET `ACTIVE` PRODUCTS AS DEFAULT
    @GetMapping(params = "!category")
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProducts(Pageable pageable) {
        ProductPageResponse products = productService.findActiveProducts(pageable);
        return ResponseEntity.ok(ApiResponse.from(
                200,
                "PRODUCT_RETRIEVED_SUCCESSFULLY",
                "Product retrieving successfully",
                OffsetDateTime.now(),
                products
        ));
    }

    // GET `ACTIVE` products by category
    @GetMapping(params = "category")
    public ResponseEntity<ApiResponse<CategoryProductPageResponse>> getProductsByCategory(
            @RequestParam(name = "category") String category,
            Pageable pageable
    ){
        ProductCategory productCategory = parseCategory(category);
        CategoryProductPageResponse products = productService.findActiveProductsByCategory(
                pageable,
                productCategory
        );

        return ResponseEntity.ok(ApiResponse.from(
                200,
                "PRODUCT_RETRIEVED_SUCCESSFULLY",
                "Product retrieving successfully",
                OffsetDateTime.now(),
                products
        ));
    }

    private ProductCategory parseCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new ProductCategoryRequiredException();
        }

        try {
            return ProductCategory.valueOf(category);
        } catch (IllegalArgumentException exception) {
            throw new InvalidProductCategoryException(category);
        }
    }
}
