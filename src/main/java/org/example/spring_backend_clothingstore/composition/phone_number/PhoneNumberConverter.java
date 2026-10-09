package org.example.spring_backend_clothingstore.composition.phone_number;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {
    @Override
    public String convertToDatabaseColumn(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.phoneNumber();
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return new PhoneNumber(phoneNumber);
    }
}
