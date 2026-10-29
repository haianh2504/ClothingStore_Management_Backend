package org.example.spring_backend_clothingstore.order.dto;

import org.example.spring_backend_clothingstore.order.entity.Order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Dữ liệu tóm tắt của một order dùng trong trang danh sách của guest. */
public record GuestOrderSummaryResponse(
        String orderCode,
        String status,
        int totalItems,
        int totalQuantity,
        BigDecimal totalPrice,
        OffsetDateTime createdAt
) {
    /** Tạo summary từ entity Order và không đưa thông tin giao hàng vào list. */
    public static GuestOrderSummaryResponse from(Order order) {
        int totalQuantity = order.getItems().stream()
                .mapToInt(item -> item.getQuantity())
                .sum();

        return new GuestOrderSummaryResponse(
                order.getOrderCode(),
                order.getStatus().name(),
                order.getItems().size(),
                totalQuantity,
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}
