package org.example.spring_backend_clothingstore.product.service;

import org.example.spring_backend_clothingstore.product.dto.response.ProductPageResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    // Find all ACTIVE products
    ProductPageResponse findActiveProducts(Pageable pageable);
}
