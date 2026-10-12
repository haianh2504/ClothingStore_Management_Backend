package org.example.spring_backend_clothingstore.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends ResourceException {
    public ProductNotFoundException(Long productId) {
        super("PRODUCT_NOT_FOUND", "Failed: Product [id=" + productId + "] not found");
    }
}
