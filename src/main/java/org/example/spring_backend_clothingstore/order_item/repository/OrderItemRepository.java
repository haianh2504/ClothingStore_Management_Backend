package org.example.spring_backend_clothingstore.order_item.repository;

import org.example.spring_backend_clothingstore.order_item.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrder_OrderCode(String orderCode);
}
