package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductCategoryRequiredException extends BusinessException {
    public ProductCategoryRequiredException() {
        super("PRODUCT_CATEGORY_REQUIRED", "Query parameter 'category' is required");
    }
}
