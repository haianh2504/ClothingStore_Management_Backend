package org.example.spring_backend_clothingstore.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.spring_backend_clothingstore.api_response_schema.ApiResponse;
import org.example.spring_backend_clothingstore.order.dto.OrderResponse;
import org.example.spring_backend_clothingstore.order.dto.PlaceOrderRequest;
import org.example.spring_backend_clothingstore.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // Place Order
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @RequestHeader("X-Guest-Id") String guestId,
            @Valid @RequestBody PlaceOrderRequest request
    ){
        OrderResponse order = orderService.placeOrder(guestId, request);
        int status = HttpStatus.CREATED.value();

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.from(
                status,
                "ORDER_CREATED",
                "Order created successfully",
                OffsetDateTime.now(),
                order
        ));
    }
}
