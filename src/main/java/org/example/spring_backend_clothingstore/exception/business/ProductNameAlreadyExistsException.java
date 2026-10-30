package org.example.spring_backend_clothingstore.exception.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Báo lỗi khi tên product mới bị trùng với product đã tồn tại.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ProductNameAlreadyExistsException extends BusinessException {
    public ProductNameAlreadyExistsException(String productName) {
        super(
                "PRODUCT_NAME_ALREADY_EXISTS",
                "Product name is already in use"
        );
    }
}
