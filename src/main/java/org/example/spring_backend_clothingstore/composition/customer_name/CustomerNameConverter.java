package org.example.spring_backend_clothingstore.composition.customer_name;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CustomerNameConverter implements AttributeConverter<CustomerName, String> {

    @Override
    public String convertToDatabaseColumn(CustomerName customerName) {
        return customerName == null ? null : customerName.name();
    }

    @Override
    public CustomerName convertToEntityAttribute(String customerName) {
        return customerName == null ? null : new CustomerName(customerName);
    }
}
