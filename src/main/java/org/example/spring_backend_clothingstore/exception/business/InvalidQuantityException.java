package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidQuantityException extends BusinessException {
    public InvalidQuantityException(String message) {
        super("INVALID_QUANTITY", message);
    }

    public static InvalidQuantityException mustBePositive() {
        return new InvalidQuantityException("Failed: Quantity must be greater than 0");
    }

    public static InvalidQuantityException mustBeAtLeastOne() {
        return new InvalidQuantityException("Failed: New Quantity must not be less than one");
    }
}
