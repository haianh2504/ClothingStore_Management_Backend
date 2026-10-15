package org.example.spring_backend_clothingstore.order.dto;

import org.example.spring_backend_clothingstore.order.entity.Order;
import org.example.spring_backend_clothingstore.order.entity.OrderStatus;
import org.example.spring_backend_clothingstore.order_item.entity.OrderItem;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public record OrderResponse(
        String orderCode,
        OrderStatus status,
        OffsetDateTime createdAt,
        List<OrderItemResponse> items,
        int totalItems,
        int totalQuantity,
        BigDecimal totalPrice
) {
    public OrderResponse {
        Objects.requireNonNull(orderCode, "Order code cannot be null");
        Objects.requireNonNull(status, "Order status cannot be null");
        Objects.requireNonNull(createdAt, "Created time cannot be null");
        Objects.requireNonNull(items, "Order items cannot be null");
        Objects.requireNonNull(totalPrice, "Total price cannot be null");
        items = List.copyOf(items);
    }

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderResponse(
                order.getOrderCode(),
                order.getStatus(),
                order.getCreatedAt(),
                items,
                items.size(),
                items.stream().mapToInt(OrderItemResponse::quantity).sum(),
                order.getTotalPrice()
        );
    }

    public record OrderItemResponse(
            Long orderItemId,
            Long variantId,
            String productName,
            String size,
            int quantity,
            String color,
            BigDecimal unitPrice
    ) {
        public OrderItemResponse {
            Objects.requireNonNull(orderItemId, "Order item id cannot be null");
            Objects.requireNonNull(variantId, "Variant id cannot be null");
            Objects.requireNonNull(productName, "Product name cannot be null");
            Objects.requireNonNull(size, "Size cannot be null");
            Objects.requireNonNull(color, "Color cannot be null");
            Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        }

        public static OrderItemResponse from(OrderItem orderItem) {
            return new OrderItemResponse(
                    orderItem.getId(),
                    orderItem.getVariant().getId(),
                    orderItem.getProductName(),
                    orderItem.getSize().name(),
                    orderItem.getQuantity(),
                    orderItem.getColor(),
                    orderItem.getUnitPrice()
            );
        }
    }
}
