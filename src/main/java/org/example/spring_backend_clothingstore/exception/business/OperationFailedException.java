package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OperationFailedException extends BusinessException {
    public OperationFailedException() {
        super("OPERATION_FAILED", "Failed: There is a problem while handling placing order");
    }

    public OperationFailedException(Throwable cause) {
        super("OPERATION_FAILED", "Failed: There is a problem while handling placing order", cause);
    }
}
