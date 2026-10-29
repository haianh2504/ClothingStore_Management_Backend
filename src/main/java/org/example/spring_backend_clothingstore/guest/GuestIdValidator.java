package org.example.spring_backend_clothingstore.guest;

import org.example.spring_backend_clothingstore.exception.business.InvalidGuestIdException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Kiểm tra guest ID có phải UUID hợp lệ do hệ thống chấp nhận hay không.
 */
@Component
public class GuestIdValidator {

    public boolean isValid(String guestId) {
        if (guestId == null || guestId.isBlank()) {
            return false;
        }

        try {
            UUID parsed = UUID.fromString(guestId);
            return parsed.toString().equalsIgnoreCase(guestId);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public void validate(String guestId) {
        if (!isValid(guestId)) {
            throw new InvalidGuestIdException();
        }
    }
}
