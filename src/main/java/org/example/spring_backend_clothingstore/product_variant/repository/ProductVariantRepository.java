package org.example.spring_backend_clothingstore.product_variant.repository;

import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    // Find by variant Id
    Optional<ProductVariant> findById(Long id);
    // Find all variants of a productId and sale status
    @EntityGraph(attributePaths = "product")
    Page<ProductVariant> findAllByProduct_IdAndProduct_SaleStatus(
            Long productId,
            SaleStatus saleStatus,
            Pageable pageable
    );

}
