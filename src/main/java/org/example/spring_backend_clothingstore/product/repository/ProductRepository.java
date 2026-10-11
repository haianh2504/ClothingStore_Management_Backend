package org.example.spring_backend_clothingstore.product.repository;

import org.example.spring_backend_clothingstore.product.entity.Product;
import org.example.spring_backend_clothingstore.product.entity.product_enum.ProductCategory;
import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find All by sale Status
    Page<Product> findAllBySaleStatus(
            SaleStatus saleStatus,
            Pageable pageable
    );

    // Find Product by category and sale status
    Page<Product> findAllByCategoryAndSaleStatus(ProductCategory category,
                                                 SaleStatus saleStatus,
                                                 Pageable pageable);
}
