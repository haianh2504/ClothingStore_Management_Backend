package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidGuestIdException extends BusinessException {
    public InvalidGuestIdException() {
        super("GUEST_ID_INVALID", "Guest ID cookie has an invalid format");
    }
}
