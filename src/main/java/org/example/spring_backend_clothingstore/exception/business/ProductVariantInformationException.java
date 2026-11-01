package org.example.spring_backend_clothingstore.exception.business;

import java.util.List;
import java.util.Map;

/** Đại diện cho dữ liệu variant không hợp lệ ngoài validation mặc định của Spring. */
public class ProductVariantInformationException extends BusinessException {
    private final List<Map<String, String>> errors;

    public ProductVariantInformationException(List<Map<String, String>> errors) {
        super("INVALID_INFORMATION_PROVIDED", "Failed: Invalid details provided");
        this.errors = List.copyOf(errors);
    }

    public List<Map<String, String>> getErrors() { return errors; }
}
