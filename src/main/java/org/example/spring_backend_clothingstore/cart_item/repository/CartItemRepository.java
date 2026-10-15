package org.example.spring_backend_clothingstore.cart_item.repository;

import org.example.spring_backend_clothingstore.cart_item.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Find all by cart Id
    @EntityGraph(attributePaths = {"variant", "variant.product"})
    Page<CartItem> findAllByCart_Id(Long cartId, Pageable pageable);

    List<CartItem> findAllByCart_Id(Long cartId);

    long countByCart_Id(Long cartId);

    @Query("select coalesce(sum(item.quantity), 0) from CartItem item where item.cart.id = :cartId")
    long sumQuantityByCartId(@Param("cartId") Long cartId);

    @Query("select coalesce(sum(item.variant.product.price * item.quantity), 0) " +
            "from CartItem item where item.cart.id = :cartId")
    BigDecimal sumSubtotalByCartId(@Param("cartId") Long cartId);

    void deleteAllByCart_Id(Long cartId);

    // Find by cart and variant Id
    Optional<CartItem> findByCart_IdAndVariant_Id(Long cartId, Long variantId);


}
