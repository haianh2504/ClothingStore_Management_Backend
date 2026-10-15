package org.example.spring_backend_clothingstore.product_variant.repository;

import org.example.spring_backend_clothingstore.product.entity.product_enum.SaleStatus;
import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    // Find by variant Id
    Optional<ProductVariant> findById(Long id);
    // Find all variants of a productId and sale status
    @EntityGraph(attributePaths = "product")
    Page<ProductVariant> findAllByProduct_IdAndProduct_SaleStatusAndDeletedAtIsNull(
            Long productId,
            SaleStatus saleStatus,
            Pageable pageable
    );

    @Modifying(flushAutomatically = true)
    @Query("update ProductVariant variant " +
            "set variant.quantity = variant.quantity - :requestedQuantity " +
            "where variant.id = :variantId " +
            "and variant.quantity >= :requestedQuantity " +
            "and variant.deletedAt is null " +
            "and variant.product.saleStatus = :saleStatus")
    int decreaseQuantityIfAvailable(
            @Param("variantId") Long variantId,
            @Param("requestedQuantity") int requestedQuantity,
            @Param("saleStatus") SaleStatus saleStatus
    );

}
