package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProductCategoryException extends BusinessException {
    public InvalidProductCategoryException(String category) {
        super(
                "INVALID_PRODUCT_CATEGORY",
                "Failed: Unsupported product category [category=" + category + "]"
        );
    }
}
