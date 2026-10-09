package org.example.spring_backend_clothingstore.order.repository;

import org.example.spring_backend_clothingstore.composition.phone_number.PhoneNumber;
import org.example.spring_backend_clothingstore.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    // Find By Order Code & PhoneNumber
    Optional<Order> findByOrderCodeAndPhoneNumber(String orderCode, PhoneNumber phoneNumber);
}
