package org.example.spring_backend_clothingstore.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Dùng khi order không tồn tại hoặc không thuộc guest hiện tại. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends ResourceException {
    public OrderNotFoundException() {
        super("ORDER_NOT_FOUND", "Order was not found");
    }
}
