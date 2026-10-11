package org.example.spring_backend_clothingstore.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.product.dto.ApiResponse;
import org.example.spring_backend_clothingstore.product.dto.response.ProductPageResponse;
import org.example.spring_backend_clothingstore.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // GET `ACTIVE` PRODUCTS AS DEFAULT
    @GetMapping
    public ApiResponse<ProductPageResponse> getProducts(Pageable pageable) {
        ProductPageResponse products = productService.findActiveProducts(pageable);
        return ApiResponse.from(
                200,
                "PRODUCT_RETRIEVED_SUCCESSFULLY",
                "Product retrieving successfully",
                OffsetDateTime.now(),
                products
        );
    }
}
