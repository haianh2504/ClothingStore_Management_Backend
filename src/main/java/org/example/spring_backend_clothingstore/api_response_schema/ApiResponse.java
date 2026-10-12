package org.example.spring_backend_clothingstore.product.dto;

import java.time.OffsetDateTime;

public record ApiResponse<T>(
        int status,
        String code,
        String message,
        OffsetDateTime timestamp,
        T data
) {
    public static <T> ApiResponse<T> from(
            int status,
            String code,
            String message,
            OffsetDateTime timestamp,
            T data
    ){
        return new ApiResponse<>(status, code, message, timestamp, data);
    }
}
