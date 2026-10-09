package org.example.spring_backend_clothingstore.product.entity.product_name;

import java.util.Objects;

public record ProductName(String name) {
    public ProductName {
        Objects.requireNonNull(name, "Product name must not be null");
        name = name.trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank" );
        } else if (!name.matches("^[\\p{L}0-9\\s\\-&(),.'\"/+]+$" )) {
            throw new IllegalArgumentException("Invalid product name!" );
        }
    }
}
