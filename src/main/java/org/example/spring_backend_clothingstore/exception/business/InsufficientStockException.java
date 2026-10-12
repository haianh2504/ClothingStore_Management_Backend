package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(int wantedQuantity, int availableQuantity) {
        super("INSUFFICIENT_STOCK", "Failed: Stock quantity insufficient - [wantedQuantity="
                + wantedQuantity
                + "] [availableQuantity="
                + availableQuantity
                + "]");
    }

    public InsufficientStockException() {
        super("INSUFFICIENT_STOCK", "Total cart quantity would exceed available stock");
    }
}
