package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ProductUnavailableException extends BusinessException {
    private final List<AvailabilityError> errors;

    public ProductUnavailableException() {
        super("PRODUCT_UNAVAILABLE", "Failed: Product is unavailable");
        this.errors = null;
    }

    public ProductUnavailableException(List<AvailabilityError> errors) {
        super("PRODUCT_UNAVAILABLE", "Failed: Product is unavailable");
        this.errors = List.copyOf(errors);
    }

    public List<AvailabilityError> getErrors() {
        return errors;
    }

    public record AvailabilityError(
            Long variantId,
            String availability
    ) {
    }
}
