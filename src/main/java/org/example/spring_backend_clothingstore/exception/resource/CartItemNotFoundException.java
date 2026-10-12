package org.example.spring_backend_clothingstore.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CartItemNotFoundException extends ResourceException {
    public CartItemNotFoundException(Long cartItemId) {
        super("CART_ITEM_NOT_FOUND", "Failed: Cart item [id=" + cartItemId + "] not found");
    }
}
