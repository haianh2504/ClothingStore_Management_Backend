package org.example.spring_backend_clothingstore.password;

import java.util.Objects;

public record PasswordHash(String passwordHash) {
    public PasswordHash{
        Objects.requireNonNull(passwordHash,"Password hash cannot be null");
        if(passwordHash.isBlank()){
            throw new IllegalArgumentException("Password hash cannot be blank");
        }
    }
}
