package org.example.spring_backend_clothingstore.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductVariantNotFoundException extends ResourceException {
    public ProductVariantNotFoundException(Long variantId) {
        super("PRODUCT_VARIANT_NOT_FOUND", "Failed: Product Variant [id=" + variantId + "] not found");
    }
}
