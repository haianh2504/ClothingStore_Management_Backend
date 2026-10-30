package org.example.spring_backend_clothingstore.product.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Các field tùy chọn được phép cập nhật trong UC10.
 */
public record UpdateProductRequest(
        @Size(max = 250, message = "Product name must not exceed 250 characters")
        @Pattern(
                regexp = "(?s).*\\S.*",
                message = "Invalid Product's name"
        )
        @Pattern(
                regexp = "^[\\p{L}0-9\\s\\-&(),.'\\\"/+]+$",
                message = "Invalid Product's name"
        )
        String productName,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        @Pattern(regexp = "(?s).*\\S.*", message = "Invalid description")
        String description,

        @Pattern(
                regexp = "TOPS|BOTTOMS|ACCESSORIES",
                message = "Invalid category"
        )
        String category,

        @DecimalMin(value = "0.01", message = "Base price must be greater than zero")
        BigDecimal basePrice
) {
    /** Đảm bảo PATCH không nhận body rỗng hoặc chỉ có các field bị bỏ qua. */
    @AssertTrue(message = "At least one product field must be provided")
    public boolean hasAtLeastOneField() {
        return productName != null
                || description != null
                || category != null
                || basePrice != null;
    }
}
