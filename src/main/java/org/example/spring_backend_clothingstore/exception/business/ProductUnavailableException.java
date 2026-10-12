package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ProductUnavailableException extends BusinessException {
    public ProductUnavailableException() {
        super("PRODUCT_UNAVAILABLE", "Failed: Product is unavailable");
    }
}
