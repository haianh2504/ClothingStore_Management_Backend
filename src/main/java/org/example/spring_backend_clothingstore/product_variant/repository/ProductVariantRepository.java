package org.example.spring_backend_clothingstore.product_variant.repository;

import org.example.spring_backend_clothingstore.product_variant.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findAllByProduct_Id(Long productId);
}
