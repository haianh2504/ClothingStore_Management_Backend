package org.example.spring_backend_clothingstore.cart_item.repository;

import org.example.spring_backend_clothingstore.cart_item.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Find all by cart Id
    List<CartItem> findAllByCart_Id(Long cartId);

    // Find by cart and variant Id
    java.util.Optional<CartItem> findByCart_IdAndVariant_Id(Long cartId, Long variantId);


}
