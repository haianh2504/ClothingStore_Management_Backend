package org.example.spring_backend_clothingstore.order.service;

import org.example.spring_backend_clothingstore.order.dto.PlaceOrderRequest;
import org.example.spring_backend_clothingstore.order.dto.OrderResponse;
import org.example.spring_backend_clothingstore.order.dto.GuestOrderDetailResponse;
import org.example.spring_backend_clothingstore.order.dto.GuestOrderHistoryResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse placeOrder(String guestId, PlaceOrderRequest request);

    // Lấy danh sách order thuộc guest hiện tại.
    GuestOrderHistoryResponse getGuestOrderHistory(String guestId, Pageable pageable);

    // Lấy chi tiết order nhưng chỉ khi order thuộc guest hiện tại.
    GuestOrderDetailResponse getGuestOrderDetail(String guestId, String orderCode);
}
