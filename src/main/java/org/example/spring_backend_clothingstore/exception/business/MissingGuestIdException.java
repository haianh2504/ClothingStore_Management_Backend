package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingGuestIdException extends BusinessException {
    public MissingGuestIdException() {
        super("GUEST_ID_MISSING", "Guest ID is missing");
    }
}
