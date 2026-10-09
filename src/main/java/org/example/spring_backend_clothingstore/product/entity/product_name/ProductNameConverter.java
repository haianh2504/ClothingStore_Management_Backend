package org.example.spring_backend_clothingstore.product.entity.product_name;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProductNameConverter implements AttributeConverter<ProductName, String> {
    @Override
    public String convertToDatabaseColumn(ProductName productName) {
        if(productName == null){
            return null;
        }
        return productName.name();
    }

    @Override
    public ProductName convertToEntityAttribute(String productName) {
        if(productName == null){
            return null;
        }
        return new ProductName(productName);
    }
}
