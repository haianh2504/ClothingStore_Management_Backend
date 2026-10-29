package org.example.spring_backend_clothingstore.order.dto;

import org.example.spring_backend_clothingstore.order.entity.Order;
import org.example.spring_backend_clothingstore.order_item.entity.OrderItem;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Dữ liệu chi tiết của order sau khi guest ownership đã được kiểm tra. */
public record GuestOrderDetailResponse(
        String orderCode,
        String status,
        String customerName,
        String phoneNumber,
        String email,
        String deliveryAddress,
        BigDecimal totalPrice,
        OffsetDateTime createdAt,
        List<OrderResponse.OrderItemResponse> items
) {
    /** Chuyển entity Order thành response chi tiết cho guest. */
    public static GuestOrderDetailResponse from(Order order) {
        List<OrderResponse.OrderItemResponse> items = order.getItems().stream()
                .map(GuestOrderDetailResponse::toItemResponse)
                .toList();

        return new GuestOrderDetailResponse(
                order.getOrderCode(),
                order.getStatus().name(),
                order.getCustomerName().name(),
                order.getPhoneNumber().phoneNumber(),
                order.getEmail(),
                order.getDeliveryAddress(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                items
        );
    }

    // Chuyển từng order item sang dữ liệu mà API chi tiết trả về.
    private static OrderResponse.OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderResponse.OrderItemResponse(
                item.getId(),
                item.getVariant().getId(),
                item.getProductName(),
                item.getSize().name(),
                item.getQuantity(),
                item.getColor(),
                item.getUnitPrice()
        );
    }
}
