package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductInactiveForCartException extends BusinessException {
    public ProductInactiveForCartException(Long variantId) {
        super(
                "PRODUCT_INACTIVE",
                "Failed: Product asssociated with variant [id=" + variantId + "] is inactive"
        );
    }

    public ProductInactiveForCartException(String message) {
        super("PRODUCT_INACTIVE", message);
    }
}
