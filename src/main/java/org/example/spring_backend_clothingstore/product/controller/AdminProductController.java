package org.example.spring_backend_clothingstore.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.example.spring_backend_clothingstore.product.dto.request.CreateProductRequest;
import org.example.spring_backend_clothingstore.product.dto.request.UpdateProductRequest;
import org.example.spring_backend_clothingstore.product.dto.response.CreatedProductResponse;
import org.example.spring_backend_clothingstore.product.service.ProductService;
import org.example.spring_backend_clothingstore.product_variant.dto.request.AddProductVariantRequest;
import org.example.spring_backend_clothingstore.product_variant.dto.response.CreatedProductVariantResponse;
import org.example.spring_backend_clothingstore.product_variant.service.ProductVariantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ProductVariantService productVariantService;

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

    /** Cập nhật một hoặc nhiều thông tin cơ bản của product. */
    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<CreatedProductResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        CreatedProductResponse product = productService.updateProduct(productId, request);
        int status = HttpStatus.OK.value();

        return ResponseEntity.ok(ApiResponse.from(
                status,
                "PRODUCT_UPDATE_SUCCESSFULLY",
                "Successfully updating product",
                OffsetDateTime.now(),
                product
        ));
    }

    /** Tạo variant cho product bằng dữ liệu multipart và ảnh upload. */
    @PostMapping(value = "/{productId}/variants", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreatedProductVariantResponse>> createProductVariant(
            @PathVariable Long productId,
            @Valid @ModelAttribute AddProductVariantRequest request
            /** @ModelAttribute yêu cầu Spring lấy các fields trong multipart/form-data và bind vào requestDTO **/
    ) {
        CreatedProductVariantResponse variant = productVariantService.createProductVariant(productId, request);
        int status = HttpStatus.CREATED.value();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.from(
                status,
                "PRODUCT_VARIANT_CREATED_SUCCESSFULLY",
                "Successfully creating new variant for a persisted product",
                OffsetDateTime.now(),
                variant
        ));
    }
}
