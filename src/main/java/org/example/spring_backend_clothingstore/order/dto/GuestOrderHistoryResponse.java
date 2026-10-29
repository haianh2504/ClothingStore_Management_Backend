package org.example.spring_backend_clothingstore.order.dto;

import org.example.spring_backend_clothingstore.api_response_schema.PageMetadata;
import org.example.spring_backend_clothingstore.order.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

/** Response chứa danh sách order của guest và thông tin phân trang. */
public record GuestOrderHistoryResponse(
        List<GuestOrderSummaryResponse> items,
        PageMetadata pagination
) {
    /** Chuyển Page<Order> sang format pagination chung của API. */
    public static GuestOrderHistoryResponse from(Page<Order> orders) {
        return new GuestOrderHistoryResponse(
                orders.map(GuestOrderSummaryResponse::from).getContent(),
                PageMetadata.from(orders)
        );
    }
}
