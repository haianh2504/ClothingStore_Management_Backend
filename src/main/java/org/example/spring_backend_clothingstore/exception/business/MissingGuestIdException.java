package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingGuestIdException extends BusinessException {
    public MissingGuestIdException() {
        super("X_GUEST_ID_MISSING", "Failed: Required header 'X-Guest-Id' is missing");
    }
}
