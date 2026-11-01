package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Báo lỗi khi product đã có variant cùng color và size. */
@ResponseStatus(HttpStatus.CONFLICT)
public class ProductVariantAlreadyExistsException extends BusinessException {
    public ProductVariantAlreadyExistsException() {
        super("PRODUCT_VARIANT_DUPLICATED", "Failed: this color and size combination already exists!");
    }
}
