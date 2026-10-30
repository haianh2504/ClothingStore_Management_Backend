package org.example.spring_backend_clothingstore.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.example.spring_backend_clothingstore.product.dto.request.CreateProductRequest;
import org.example.spring_backend_clothingstore.product.dto.response.CreatedProductResponse;
import org.example.spring_backend_clothingstore.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

/**
 * Cung cấp các API administrator dùng để quản lý product.
 */
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;

    /** Tạo product mới và trả về product với saleStatus INACTIVE. */
    @PostMapping
    public ResponseEntity<ApiResponse<CreatedProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        CreatedProductResponse product = productService.createProduct(request);
        int status = HttpStatus.CREATED.value();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.from(
                status,
                "PRODUCT_CREATED_SUCCESSFULLY",
                "Successfully creating new product",
                OffsetDateTime.now(),
                product
        ));
    }
}
