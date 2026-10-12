package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class CartEmptyException extends BusinessException {
    public CartEmptyException() {
        super("CART_EMPTY", "Failed: There are no items in cart to place order");
    }
}
