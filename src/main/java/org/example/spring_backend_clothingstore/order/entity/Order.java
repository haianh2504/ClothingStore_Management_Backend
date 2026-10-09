package org.example.spring_backend_clothingstore.order.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.spring_backend_clothingstore.composition.phone_number.PhoneNumber;
import org.example.spring_backend_clothingstore.composition.customer_name.CustomerName;
import org.example.spring_backend_clothingstore.composition.customer_name.CustomerNameConverter;
import org.example.spring_backend_clothingstore.order_item.entity.OrderItem;
import org.example.spring_backend_clothingstore.composition.phone_number.PhoneNumberConverter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Order {

    @Id
    @Column(name = "order_code", nullable = false, length = 250)
    private String orderCode;

    @Convert(converter = CustomerNameConverter.class)
    @Column(name = "customer_name", nullable = false, length = 255)
    private CustomerName customerName;

    @Convert(converter = PhoneNumberConverter.class)
    @Column(name = "phone_number", nullable = false, length = 50)
    private PhoneNumber phoneNumber;

    @Column(name = "email", length = 250)
    @Email
    private String email;

    @Column(name = "delivery_address", nullable = false, length = 255)
    private String deliveryAddress;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    @DecimalMin(value = "0.00")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();
}
