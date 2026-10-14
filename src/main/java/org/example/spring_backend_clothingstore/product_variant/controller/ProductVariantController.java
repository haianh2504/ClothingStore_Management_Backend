package org.example.spring_backend_clothingstore.product_variant.controller;

import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.example.spring_backend_clothingstore.product_variant.dto.ProductVariantResponse;
import org.example.spring_backend_clothingstore.product_variant.service.ProductVariantService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/products/{productId}/variants")
@RequiredArgsConstructor
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    // GET all ACTIVE variants of an ACTIVE product
    @GetMapping
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getProductVariantsByProductId(
            @PathVariable Long productId,
            Pageable pageable
    ) {
        ProductVariantResponse data = productVariantService.getProductVariantsByProductId(
                productId,
                pageable
        );

        return ResponseEntity.ok(ApiResponse.from(
                HttpStatus.OK.value(),
                "PRODUCT_VARIANT_RETRIEVED_SUCCESSFULLY",
                "Product's Variants retrieving successfully",
                OffsetDateTime.now(),
                data
        ));
    }
}
