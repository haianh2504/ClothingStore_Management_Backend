package org.example.spring_backend_clothingstore.exception.response;

import java.time.OffsetDateTime;

public record ErrorResponse(
        int status,
        String code,
        String message,
        OffsetDateTime timestamp
) {
    public static ErrorResponse from(
            int status,
            String code,
            String message
    ) {
        return new ErrorResponse(
                status,
                code,
                message,
                OffsetDateTime.now()
        );
    }
}
