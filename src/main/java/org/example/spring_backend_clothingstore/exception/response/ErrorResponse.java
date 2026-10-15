package org.example.spring_backend_clothingstore.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String code,
        String message,
        OffsetDateTime timestamp,
        List<?> error
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
                OffsetDateTime.now(),
                null
        );
    }

    public static ErrorResponse from(
            int status,
            String code,
            String message,
            List<?> error
    ) {
        return new ErrorResponse(
                status,
                code,
                message,
                OffsetDateTime.now(),
                error
        );
    }
}
