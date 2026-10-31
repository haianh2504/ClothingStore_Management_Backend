package org.example.spring_backend_clothingstore.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Dữ liệu client gửi khi administrator tạo product mới.
 */
public record CreateProductRequest(
        @NotNull(message = "ProductName is required")
        @Size(max = 250, message = "Product name must not exceed 250 characters")
        @Pattern(
                regexp = "(?s)(?=.*\\S)^[\\p{L}0-9\\s\\-&(),.'\\\"/+]+$",
                message = "Invalid Product's name"
        )
        String productName,

        @NotNull(message = "Description is required")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        @Pattern(regexp = "(?s).*\\S.*", message = "Invalid description")
        String description,

        @NotNull(message = "Category is required")
        @Pattern(
                regexp = "TOPS|BOTTOMS|ACCESSORIES",
                message = "Invalid category"
        )
        String category,

        @NotNull(message = "BasePrice is required")
        @DecimalMin(value = "0.00", message = "Invalid base price")
        BigDecimal basePrice
) {
}
