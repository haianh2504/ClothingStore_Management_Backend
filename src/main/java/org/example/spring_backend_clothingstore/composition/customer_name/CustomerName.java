package org.example.spring_backend_clothingstore.composition.customer_name;

import java.util.Objects;
import java.text.Normalizer;
import java.util.regex.Pattern;

public record CustomerName(String name) {

    private static final int MAX_LENGTH = 255;
    private static final Pattern VALID_NAME = Pattern.compile(
            "^[A-Za-zĐđ]+(?:[ '-][A-Za-zĐđ]+)*$"
    );

    public CustomerName {
        Objects.requireNonNull(name, "Customer name must not be null");

        name = name.trim().replaceAll("\\s+", " ");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be blank");
        }
        if (name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Customer name must not exceed 255 characters");
        }
        String decomposedName = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        if (!VALID_NAME.matcher(decomposedName).matches()) {
            throw new IllegalArgumentException(
                    "Customer name may contain only English/Vietnamese letters, spaces, hyphens, and apostrophes"
            );
        }
    }
}
