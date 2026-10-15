package org.example.spring_backend_clothingstore.order.service;

import org.example.spring_backend_clothingstore.order.dto.PlaceOrderRequest;
import org.example.spring_backend_clothingstore.order.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(String guestId, PlaceOrderRequest request);
}
