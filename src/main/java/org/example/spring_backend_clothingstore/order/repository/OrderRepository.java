package org.example.spring_backend_clothingstore.order.repository;

import org.example.spring_backend_clothingstore.composition.phone_number.PhoneNumber;
import org.example.spring_backend_clothingstore.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    // Lấy danh sách đơn của một guest, đơn mới nhất được trả về trước.
    Page<Order> findByGuestIdOrderByCreatedAtDesc(String guestId, Pageable pageable);

    // Chỉ lấy chi tiết khi order code và guest ID cùng khớp.
    @EntityGraph(attributePaths = "items")
    Optional<Order> findByOrderCodeAndGuestId(String orderCode, String guestId);

    // Tìm order để khôi phục khi guest có order code và số điện thoại.
    @EntityGraph(attributePaths = "items")
    Optional<Order> findByOrderCodeAndPhoneNumber(String orderCode, PhoneNumber phoneNumber);
}
