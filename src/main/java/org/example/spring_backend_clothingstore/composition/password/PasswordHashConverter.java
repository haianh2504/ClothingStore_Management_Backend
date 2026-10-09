package org.example.spring_backend_clothingstore.composition.password;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PasswordHashConverter implements AttributeConverter<PasswordHash, String> {

    @Override
    public String convertToDatabaseColumn(PasswordHash passwordHash) {
        return passwordHash == null ? null : passwordHash.passwordHash();
    }

    @Override
    public PasswordHash convertToEntityAttribute(String passwordHash) {
        return passwordHash == null ? null : new PasswordHash(passwordHash);
    }
}
