package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ProductInactiveException extends BusinessException {
    public ProductInactiveException(Long productId) {
        super("PRODUCT_INACTIVE", "Failed: Product [id=" + productId + "] is inactive");
    }
}
