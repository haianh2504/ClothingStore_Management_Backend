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

    // Kiểm tra nhanh tên product đã được sử dụng hay chưa.
    boolean existsByName_Name(String name);

    // Kiểm tra tên đã thuộc product khác hay chưa khi cập nhật.
    boolean existsByName_NameAndIdNot(String name, Long productId);

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
