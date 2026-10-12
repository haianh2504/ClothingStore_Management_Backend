package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCustomerInformationException extends BusinessException {
    public InvalidCustomerInformationException() {
        super("INVALID_INFORMATION_PROVIDED", "Failed: Invalid details provided");
    }

    public InvalidCustomerInformationException(String message) {
        super("INVALID_INFORMATION_PROVIDED", message);
    }
}
